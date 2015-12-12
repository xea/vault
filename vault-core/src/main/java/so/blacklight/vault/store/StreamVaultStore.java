package so.blacklight.vault.store;

import static fj.data.List.list;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import fj.F;
import fj.Unit;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import so.blacklight.vault.Credential;
import so.blacklight.vault.Credentials;
import so.blacklight.vault.Vault;
import so.blacklight.vault.VaultStore;
import so.blacklight.vault.collection.Tuple2;
import so.blacklight.vault.crypto.Crypto;
import so.blacklight.vault.crypto.CryptoImpl;
import so.blacklight.vault.crypto.EncryptionParameter;
import so.blacklight.vault.io.VaultInputStream;
import so.blacklight.vault.io.VaultOutputStream;
import so.blacklight.vault.io.VaultRecord;

/**
 * A vault store that uses vault VaultInputStream and VaultOutputStream internally to load/save vault data
 */
public class StreamVaultStore implements VaultStore {

    @Override
    public Either<String, Boolean> save(Vault vault, Credentials credentials, File vaultFile) {
        if (!vaultFile.exists() || vaultFile.canWrite()) {
            try {
                final FileOutputStream fos = new FileOutputStream(vaultFile);

                final Either<String, Boolean> result = save(vault, credentials, fos);

                fos.close();

                return result;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return Either.left(e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
                return Either.left(e.getMessage());
            }
        }

        return Either.left("Vault file does already exist and is not writable");
    }

    @Override
    public Either<String, Boolean> save(Vault vault, Credentials credentials, OutputStream outputStream) {
        try {
            final ByteArrayOutputStream safetyBuffer = new ByteArrayOutputStream();
            final VaultOutputStream vos = new VaultOutputStream(safetyBuffer);
            final Layout layout = new Layout(vault, credentials);

            vos.writeMagicBytes();
            vos.writeLayout(layout);

            final List<Either<String, List<VaultRecord>>> result = saveInternal(vault, credentials);

            if (result.find(Either::isLeft).isSome()) {
                return Either.left(result.find(Either::isLeft).some().left().value());
            } else {
                result.map(r -> r.right().value()).map(l -> l.foreach(r -> writeBlock(vos, r)));
            }

            vos.close();

            // All's fine if the end's fine
            outputStream.write(safetyBuffer.toByteArray());

            return Either.right(true);
        } catch (IOException e) {
            e.printStackTrace();
            return Either.left(e.getMessage());
        }
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, File vaultFile) {
        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final FileInputStream fis = new FileInputStream(vaultFile);
                final Either<String, Vault> maybeVault = load(credentials, fis);

                fis.close();

                return maybeVault;
            } catch (FileNotFoundException e) {
                return Either.left(e.getMessage());
            } catch (IOException e) {
                return Either.left(e.getMessage());
            }
        }

        return Either.left("ERROR: File not found or not readable: " + vaultFile.getAbsolutePath());
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, InputStream inputStream) {
        try {
            final List<Credential> cl = list(credentials.getCredentials());
            final List<VaultRecord> records = readAllRecords(inputStream, (r -> r.count() == cl.length()));
            final List<Either<String, Vault>> unlocked = decryptRecords(cl, records).filter(e -> e.isRight());

            if (unlocked.length() > 0) {
                return unlocked.index(0);
            } else {
                return Either.left("Couldn't decrypt vault with the given credentials");
            }
        } catch (IOException e) {
            return Either.left("Error during loading: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
	private List<Either<String, List<VaultRecord>>> saveInternal(final Vault vault, final Credentials credentials) {
        final List<Credential> creds = list(credentials.getCredentials());

        // for each vault segment type we generate a list of credential combinations
        final List<F<List<Credential>, List<List<Credential>>>> fs = list(
                l -> list(new List[] { l }),
                l -> l.map(c -> l.filter(cf -> !cf.equals(c))),
                l -> l.map(c1 -> l.map(c2 -> l.filter(cf -> !cf.equals(c1) && !cf.equals(c2) && !c1.equals(c2))))
                        .foldRight((currentItem, foldList) -> foldList.append(currentItem), List.<List<Credential>>list())
                        .filter(fl -> fl.length() > 0).nub()
        );

        final List<F<Vault, Optional<Vault>>> fv = list(
                v -> Optional.of(v),
                v -> v.getRecoverySegment(),
                v -> v.getDegradedSegment()
        );

        final F<List<Credential>, List<List<Credential>>> emptyList = l -> list();

        // Override time-consuming operations with trivial ones for those steps when we won't be encrypting anything
        final List<F<List<Credential>, List<List<Credential>>>> ff = fv.map(f -> f.f(vault).isPresent()).zipWith(fs, (a, b) -> a ? b : emptyList);

        // for each vault record we generate new encryption parameters and then encrypt the records
        final List<Either<String, List<VaultRecord>>> map = ff.map(f ->
                f.f(creds).map(list ->
                        list.map(c -> new EncryptionParameter(c))))
                .zipWith(fv.map(f ->
                        f.f(vault)), (l, v) -> new EncTuple(v, l))
                .filter(t -> t.first().isPresent()).map(tuple -> generateRecords(tuple));
        return map;
    }
    
    private Either<String, List<VaultRecord>> generateRecords(final EncTuple tuple) {
        final Optional<Vault> maybeVault = tuple.first();
        final List<List<EncryptionParameter>> params = tuple.second();

        if (maybeVault.isPresent()) {
            final Vault currentVault = maybeVault.get();

            List<Either<String, VaultRecord>> result = params.map(param -> generateRecord(currentVault, param));
            Option<String> firstError = result.filter(Either::isLeft).map(e -> e.left().value()).find(i -> true);

            if (firstError.isNone()) {
                return Either.right(result.map(r -> r.right().value()));
            } else {
                return Either.left(firstError.some());
            }
        }

        return Either.left("ERROR: No vault is present");
    }

    private Either<String, VaultRecord> generateRecord(final Vault vault, List<EncryptionParameter> params) {
        final Crypto<Vault> crypto = new CryptoImpl<>();
        Either<String, byte[]> encrypt = crypto.encrypt(vault, params.toJavaList());

        if (encrypt.isRight()) {
            final VaultRecord record = new VaultRecord(encrypt.right().value());

            record.addIvs(params.map(EncryptionParameter::getIv).toJavaList());
            record.addSalts(params.map(EncryptionParameter::getSalt).toJavaList());

            return Either.right(record);
        } else {
            return Either.left(encrypt.left().value());
        }
    }

    private Unit writeBlock(final VaultOutputStream out, VaultRecord record) {
        try {
            out.writeBlock(record);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Unit.unit();
    }


    private List<VaultRecord> readAllRecords(final InputStream in, final F<VaultRecord, Boolean> f) throws IOException {
        return readAllRecords(in).filter(f);
    }

    private List<VaultRecord> readAllRecords(final InputStream in) throws IOException {
        final VaultInputStream vis = new VaultInputStream(in);
        final List<VaultRecord> allRecords = vis.readAll();
        vis.close();

        return allRecords;
    }

    private List<Either<String, Vault>> decryptRecords(final List<Credential> credentials, final List<VaultRecord> records) {
        final Crypto<Vault> crypto = new CryptoImpl<>();
        final List<Either<String, Vault>> result = records.map(r -> {
            final byte[][] ivs = r.getIvs();
            final byte[][] salts = r.getSalts();

            // we're leveraging a mutable collection here
            final java.util.List<EncryptionParameter> params = new ArrayList<>();

            for (int i = 0; i < r.count(); i++) {
                final Credential credential = credentials.index(i);
                params.add(new EncryptionParameter(credential, ivs[i], salts[i]));
            }

            // credentials are always sorted for encryption, here we manually reverse the order for decryption
            Collections.reverse(params);

            return crypto.decrypt(r.getBlock(), params);
        });

        return result;
    }
    
    /**
     * Blatant workaround/hack class for ensuring type safety where type erasure would make types difficult to infer
     */
    private static class EncTuple extends Tuple2<Optional<Vault>, List<List<EncryptionParameter>>> {

		public EncTuple(Optional<Vault> t, List<List<EncryptionParameter>> u) {
			super(t, u);
		}
    }

}
