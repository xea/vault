package so.blacklight.vault;

public class Credentials {

    private final transient String passphrase;

    private final transient String otp;

    public Credentials(final String passphrase, final String otp) {
        this.passphrase = passphrase;
        this.otp = otp;
    }

    @Override
    public String toString() {
        int maxIndex = Math.min(otp.length(), 7);
        return passphrase + otp.substring(0, maxIndex);
    }
}
