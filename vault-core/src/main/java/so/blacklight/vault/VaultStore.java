package so.blacklight.vault;

import java.io.*;
import java.util.Optional;

public class VaultStore {

    public Optional<Vault> load(final File file) throws IOException, ClassNotFoundException {
        return load(file.getAbsolutePath());
    }

    /**
     * Attempts to laod a Vault object from a file at the given path
     *
     * @param filename path to the file to load
     * @return A Vault object if the loading was successful, otherwise Optional.empty()
     * @throws IOException if there was an I/O error during loading
     * @throws ClassNotFoundException Class of a serialized object cannot be found
     */
    public Optional<Vault> load(final String filename) throws IOException, ClassNotFoundException {
        final InputStream is = new FileInputStream(filename);

        final Optional<Vault> result = load(is);
        is.close();

        return result;
    }

    /**
     * Attempts to load the Vault from the given input stream.
     *
     * @param is input stream to load to vault from
     * @return A Vault object if the loading was successful, otherwise Optional.empty()
     * @throws IOException if there was an I/O error during loading
     * @throws ClassNotFoundException Class of a serialized object cannot be found
     */
    public Optional<Vault> load(final InputStream is) throws IOException, ClassNotFoundException {
        final ObjectInputStream ois = new ObjectInputStream(is);
        final Object readObject = ois.readObject();
        final Optional<Vault> result;

        if (readObject instanceof Vault) {
            result = Optional.of((Vault) readObject);
        } else {
            result = Optional.empty();
        }

        ois.close();

        return result;
    }

    public void save(final Vault Vault, final File file) throws IOException {
        final OutputStream os = new FileOutputStream(file);
        save(Vault, os);
        os.close();
    }

    public void save(final Vault Vault, final OutputStream os) throws IOException {
        final ObjectOutputStream oos = new ObjectOutputStream(os);

        oos.writeObject(Vault);

        oos.close();
    }
}
