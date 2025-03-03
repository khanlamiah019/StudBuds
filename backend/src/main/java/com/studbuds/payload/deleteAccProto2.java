// DeleteAccountRequest class for user account deletion
public class DeleteAccountRequest {
   private String email;
   private String password;
   
   // Getters and setters
   public String getEmail() { return email; }
   public void setEmail(String email) {
       if (email.endsWith("@cooper.edu")) {
           this.email = email;
       } else {
           throw new IllegalArgumentException("Email must be a @cooper.edu address");
       }
   }
   
   public String getPassword() { return password; }
   public void setPassword(String password) {
       if (password.length() >= 9) {
           this.password = password;
       } else {
           throw new IllegalArgumentException("Password must be at least 9 characters long");
       }
   }
}
