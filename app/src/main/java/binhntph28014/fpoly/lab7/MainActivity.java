package binhntph28014.fpoly.lab7;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChat;
    private Button mButtonLogin;
    private Button mButtonChat;
    private EditText mEditTextName;
    private List<String> mListMessages;
    private ChatAdapter mChatAdapter;
    private final String URL_SERVER = "http://192.168.56.1:3000";
    private Socket mSocket;

//    {
//        try {
//            mSocket = IO.socket(URL_SERVER);
//            mSocket.connect();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ket noi moi
        IO.Options options = new IO.Options(); options.forceNew = true;
        try {
            mSocket = IO.socket(URL_SERVER, options);
            mSocket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        mSocket.on("receiver_message", onNewMessage);
        mButtonLogin = findViewById(R.id.btn_login);
        mButtonChat = findViewById(R.id.btn_chat);
        mEditTextName = findViewById(R.id.edt_name);
        mListMessages = new ArrayList<>();
        mRecyclerViewChat = findViewById(R.id.rc_chat);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerViewChat.setLayoutManager(layoutManager);
        mChatAdapter = new ChatAdapter( mListMessages);
        mRecyclerViewChat.setAdapter(mChatAdapter);
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("user_login", mEditTextName.getText().toString());


            }
        });

        mButtonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSocket.emit("send_message", mEditTextName.getText().toString());
            }
        });

    }

    private Emitter.Listener onNewMessage= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject data = (JSONObject) args[0];
                    String message;

                    message = data.optString("data");
                    Log.d("Chat","=> "+message);
                    mChatAdapter.addMessage(message);

                }
            });
        }
    };
}