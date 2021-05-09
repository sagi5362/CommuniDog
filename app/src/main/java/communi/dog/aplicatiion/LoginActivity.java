package communi.dog.aplicatiion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    private HashMap<String, String> allUsers;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        allUsers = new HashMap<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference iDsRef = database.getReference("Users");
        this.usersRef = iDsRef;

        Intent activityIntent = getIntent();

        EditText idEditText = findViewById(R.id.input_id_login);
        EditText userPassword = findViewById(R.id.user_password);

        TextView to_register_btn = findViewById(R.id.register_now);
        to_register_btn.setOnClickListener(v -> startActivity(new Intent(this, RegisterActivity.class)));

        ImageView btnGoToMap = findViewById(R.id.buttonTempGoToMap);
        btnGoToMap.setOnClickListener((v) -> {
            Intent intent = new Intent(this, MapScreenActivity.class);
            if (activityIntent.hasExtra("map_old_state")) {
                intent.putExtra("map_old_state", activityIntent.getSerializableExtra("map_old_state"));
            }
            startActivity(intent);
        });

        findViewById(R.id.loginConstraintLayout).setOnClickListener(v -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            idEditText.requestFocus();
            imm.hideSoftInputFromWindow(idEditText.getWindowToken(), 0);
            idEditText.clearFocus();
        });

        findViewById(R.id.login_button).setOnClickListener(v -> { //todo: check
            if (isUserExists(idEditText, userPassword)){
                // todo: Move to other activity?
            }
            else{
                //todo: a Toast?
            }
        });

        readDataIdsInUse(new FirebaseCallback(){
            @Override
            public void onCallback(HashMap<String, String> allUsers) {
            }
        });
    }

    private boolean isUserExists(EditText id, EditText password) {
        String inputId = id.getText().toString();
        String inputPassword = password.getText().toString();
        return allUsers.get(inputId) != null &&
                Objects.equals(allUsers.get(inputId), inputPassword);
    }

    private interface FirebaseCallback{
        void onCallback(HashMap<String, String> allUsers);
    }

    private void readDataIdsInUse(LoginActivity.FirebaseCallback firebaseCallback){
        ValueEventListener valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    if (ds != null) {
                        String id = ds.child("id").getValue(String.class);
                        String password = ds.child("password").getValue(String.class);
                            allUsers.put(id, password);
                    }
                }
                firebaseCallback.onCallback(allUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        usersRef.addListenerForSingleValueEvent(valueEventListener);
    }
}