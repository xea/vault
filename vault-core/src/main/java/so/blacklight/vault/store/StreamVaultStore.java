package so.blacklight.vault.store;

import fj.F;
import fj.F2;
import fj.Unit;
import fj.data.Either;
import fj.data.List;
import so.blacklight.vault.*;
import so.blacklight.vault.io.VaultInputStream;
import so.blacklight.vault.io.VaultOutputStream;
import so.blacklight.vault.io.VaultRecord;

import java.io.*;
import java.util.*;

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

    @Override
    public void save(Vault vault, Credentials credentials, OutputStream outputStream) {
        try {
            final ByteArrayOutputStream safetyBuffer = new ByteArrayOutputStream();
            final VaultOutputStream vos = new VaultOutputStream(safetyBuffer);
            final Layout layout = new Layout(vault, credentials);

            vos.writeMagicBytes();
            vos.writeLayout(layout);

            final List<Credential> cl = list(credentials.getCredentials());
            final List<List<Credential>> primaryCredentials = List.<List<Credential>>list(cl);
            final List<List<Credential>> recoveryCredentials = cl.map(c -> cl.filter(cf -> !cf.equals(c)));
            final List<List<Credential>> degradedCredentials = cl.map(c1 -> cl.map(c2 -> cl.filter(cf -> !cf.equals(c1) && !cf.equals(c2) && !c1.equals(c2))))
                    .foldRight((currentItem, foldList) -> {
                        return foldList.append(currentItem);
                    }, List.<List<Credential>>list()).filter(l -> l.length() > 0).nub();

            final List<List<EncryptionParameters>> primaryParams = primaryCredentials.map(list -> list.map(EncryptionParameters::new));
            final List<List<EncryptionParameters>> recoveryParams = recoveryCredentials.map(list -> list.map(EncryptionParameters::new));
            final List<List<EncryptionParameters>> degradedParams = degradedCredentials.map(list -> list.map(EncryptionParameters::new));

            final Optional<Vault> primaryVault = Optional.of(vault);
            final Optional<Vault> recoveryVault = vault.getRecoverySegment();
            final Optional<Vault> degradedVault = vault.getDegradedSegment();

            final List<Tuple2<Optional<Vault>, List<List<EncryptionParameters>>>> batches = list(
                    new Tuple2<>(primaryVault, primaryParams),
                    new Tuple2<>(recoveryVault, recoveryParams),
                    new Tuple2<>(degradedVault, degradedParams)
            );

            final Crypto<Vault> crypto = new CryptoImpl<>();

            final F2<Vault, List<EncryptionParameters>, Either<String, VaultRecord>> f = (currentVault, params) -> {
                Either<String, byte[]> encrypt = crypto.encrypt(currentVault, params.toJavaList());

                if (encrypt.isRight()) {
                    final VaultRecord record = new VaultRecord(encrypt.right().value());

                    record.addIvs(params.map(p -> p.getIv()).toJavaList());
                    record.addSalts(params.map(p -> p.getSalt()).toJavaList());

                    return Either.right(record);
                } else {
                    return Either.left(encrypt.left().value());
                }
            };

            batches.foreach(tuple -> {
                final Optional<Vault> maybeVault = tuple.first();
                final List<List<EncryptionParameters>> params = tuple.second();

                if (maybeVault.isPresent()) {
                    final Vault currentVault = maybeVault.get();

                    List<Either<String, VaultRecord>> result = params.map(param -> f.f(currentVault, param));
                    List<Either<String, VaultRecord>> errors = result.filter(r -> r.isLeft());

                    if (errors.isEmpty()) {
                        // yay
                        result.map(r -> r.right().value()).toJavaList().forEach(record -> writeBlock(vos, record));
                    } else {
                        // boo
                    }
                }

                return Unit.unit();
            });

            vos.close();
            safetyBuffer.write(safetyBuffer.toByteArray());

            // All's fine if the end's fine
            outputStream.write(safetyBuffer.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeBlock(final VaultOutputStream out, VaultRecord record) {
        try {
            out.writeBlock(record);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<VaultRecord> generateRecords(Vault vault, Credentials credentials) {
        final Crypto<Vault> crypto = new CryptoImpl<>();


        return null;
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, File vaultFile) {
        if (vaultFile.exists() && vaultFile.canRead()) {
            try {
                final FileInputStream fis = new FileInputStream(vaultFile);

                final Either<String, Vault> maybeVault = load(credentials, fis);

                fis.close();

                return maybeVault;
            } catch (Exception e) {
                return Either.left(e.getMessage());
            }
        }

        return Either.left("ERROR: File not found or not readable: " + vaultFile.getAbsolutePath());
    }

    @Override
    public Either<String, Vault> load(Credentials credentials, InputStream inputStream) {
        try {
            final VaultInputStream vis = new VaultInputStream(inputStream);
            final List<VaultRecord> allRecords = vis.readAll();
            vis.close();

            final List<Credential> cl = list(credentials.getCredentials());
            final List<VaultRecord> records = allRecords.filter(r -> r.count() == cl.length());

            final Crypto<Vault> crypto = new CryptoImpl<>();
            final List<Either<String, Vault>> results = records.map(r -> {
                final byte[][] ivs = r.getIvs();
                final byte[][] salts = r.getSalts();

                final java.util.List<EncryptionParameters> params = new ArrayList<>();

                for (int i = 0; i < r.count(); i++) {
                    params.add(new EncryptionParameters(cl.index(i), ivs[i], salts[i]));
                }

                Either<String, Vault> decrypt = crypto.decrypt(r.getBlock(), params);

                return decrypt;
            });

            final List<Either<String, Vault>> unlocked = results.filter(e -> e.isRight());

            if (unlocked.length() > 0) {
                return unlocked.index(0);
            } else {
                return Either.left("Sorry :(");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Either.left("I've got no idea what I'm doing");
    }

}
