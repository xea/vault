package so.blacklight.vault.security;

public class PasswordWithOTPCredentials implements Credentials {

    public static final long serialVersionUID = 89723463987412L;

    private final char[] name;

    private final char[] password;

    private final char[] otp;

    public PasswordWithOTPCredentials(final char[] name, final char[] password, final char[] otp) {
        this.name = name;
        this.password = password;
        this.otp = otp;
    }

    @Override
    public char[] generateKey() {
        return new char[0];
    }

    public char[] getName() {
        return name;
    }

    public char[] getPassword() {
        return password;
    }

    public char[] getOtp() {
        return otp;
    }

    public PasswordWithOTPCredentials setName(final char[] newName) {
        return new PasswordWithOTPCredentials(newName, password, otp);
    }

    public PasswordWithOTPCredentials setPassword(final char[] newPassword) {
        return new PasswordWithOTPCredentials(name, newPassword, otp);
    }

    public PasswordWithOTPCredentials setOtp(final char[] newOtp) {
        return new PasswordWithOTPCredentials(name, password, newOtp);
    }
}
