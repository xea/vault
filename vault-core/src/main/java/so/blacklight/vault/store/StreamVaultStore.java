package so.blacklight.vault.store;

import fj.F;
import fj.Unit;
import fj.data.Either;
import fj.data.List;
import fj.data.Option;
import so.blacklight.vault.*;
import so.blacklight.vault.collection.Tuple2;
import so.blacklight.vault.io.VaultInputStream;
import so.blacklight.vault.io.VaultOutputStream;
import so.blacklight.vault.io.VaultRecord;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import static fj.data.List.list;

public class StreamVaultStore implements VaultStore {

    @Override
    public void save(Vault vault, Credentials credentials, File vaultFile) {
        if (!vaultFile.exists() || vaultFile.canWrite()) {
            try {
                final FileOutputStream fos = new FileOutputStream(vaultFile);

                save(vault, credentials, fos);

                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Either<String, List<VaultRecord>>> saveInternal(final Vault vault, final Credentials credentials) {
        final List<Credential> creds = list(credentials.getCredentials());

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

        List<Either<String, List<VaultRecord>>> map = fs.map(f ->
                f.f(creds).map(list ->
                        list.map(EncryptionParameters::new)))
                .zipWith(fv.map(f ->
                        f.f(vault)), (l, v) -> new Tuple2(v, l)).map(tuple -> generateRecords(tuple));
        return map;
    }

    @Override
    public void save(Vault vault, Credentials credentials, OutputStream outputStream) {
        try {
            final ByteArrayOutputStream safetyBuffer = new ByteArrayOutputStream();
            final VaultOutputStream vos = new VaultOutputStream(safetyBuffer);
            final Layout layout = new Layout(vault, credentials);

            vos.writeMagicBytes();
            vos.writeLayout(layout);

            final List<Either<String, List<VaultRecord>>> result = saveInternal(vault, credentials);

            if (result.find(Either::isLeft).length() > 0) {
                // TODO better error handling
            } else {
                result.map(r -> r.right().value()).map(l -> l.foreach(r -> writeBlock(vos, r)));
            }

            vos.close();

            // All's fine if the end's fine
            outputStream.write(safetyBuffer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Either<String, List<VaultRecord>> generateRecords(final Tuple2<Optional<Vault>, List<List<EncryptionParameters>>> tuple) {
        final Optional<Vault> maybeVault = tuple.first();
        final List<List<EncryptionParameters>> params = tuple.second();

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

        return Either.left("ERROR: No vault present");
    }

    private Either<String, VaultRecord> generateRecord(final Vault vault, List<EncryptionParameters> params) {
        final Crypto<Vault> crypto = new CryptoImpl<>();
        Either<String, byte[]> encrypt = crypto.encrypt(vault, params.toJavaList());

        if (encrypt.isRight()) {
            final VaultRecord record = new VaultRecord(encrypt.right().value());

            record.addIvs(params.map(p -> p.getIv()).toJavaList());
            record.addSalts(params.map(p -> p.getSalt()).toJavaList());

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
                return Either.left("Sorry :(");
            }
        } catch (IOException e) {
            return Either.left("Error during loading: " + e.getMessage());
        }
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
            final java.util.List<EncryptionParameters> params = new ArrayList<>();

            for (int i = 0; i < r.count(); i++) {
                params.add(new EncryptionParameters(credentials.index(i), ivs[i], salts[i]));
            }

            Collections.reverse(params);

            return crypto.decrypt(r.getBlock(), params);
        });

        return result;
    }
}