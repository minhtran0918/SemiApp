package org.semi.utils;

import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.semi.R;

import java.util.Arrays;
import java.util.List;

public final class SignInUtils {
    public static final int SIGN_IN_CODE = 0;

    private SignInUtils() {
    }

    public static FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public static boolean userAuthenticated(){
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }
    public static void userLogout(){
        FirebaseAuth.getInstance().signOut();
    }

    public static Intent intentCallLogin() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build());

        // Create and launch sign-in intent
        return AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setTosAndPrivacyPolicyUrls(MyApp.getContext().getString(R.string.temrs_and_conditions_url),
                        MyApp.getContext().getString(R.string.privacy_policy_url))
                .setTheme(R.style.LoginTheme)
                .setLogo(R.drawable.background_semi_logo)
                .build();
    }
  /*  public static void getIdToken(final IResult<GetTokenResult> result) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            Task<GetTokenResult> task = user.getIdToken(false);
            task.addOnSuccessListener(new OnSuccessListener<GetTokenResult>() {
                @Override
                public void onSuccess(GetTokenResult getTokenResult) {
                    result.onResult(getTokenResult);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    result.onFailure(e);
                }
            });
        } else {
            result.onResult(null);
        }
    }
*/
    /*public static void signInWithGoogle(Activity activity) {
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(activity.getString(R.string.web_client_id))
                .requestScopes(new Scope(Scopes.PROFILE))
                .build();
        GoogleSignInClient client = GoogleSignIn.getClient(activity, options);
        Intent signInIntent = client.getSignInIntent();
        activity.startActivityForResult(signInIntent, SIGN_IN_CODE);
    }*/

   /* public static void firebaseAuthWithGoogle(GoogleSignInAccount account, final IResult<AuthResult> result) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        result.onResult(authResult);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        result.onFailure(e);
                    }
                });
    }*/

}
