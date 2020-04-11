package com.example.abdelrahmantarek.awspoc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AmazonS3Client s3;
    TransferUtility transferUtility;
    File file = new File("/storage/emulated/0/Download/fb.docx");
    File downloaded = new File("/storage/emulated/0/Download/MY.docx");
    Button up, down;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        up = findViewById(R.id.upload_btn);
        down = findViewById(R.id.download_btn);
        up.setOnClickListener(this);
        down.setOnClickListener(this);

        credentialProvider();
        setTransferUtility();
    }

    public void credentialProvider() {
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                "us-east-1:72e60533-8780-47c4-a4aa-9b4c7b24e0a0", // Identity pool ID
                Regions.US_EAST_1 // Region
        );

        setAmazonS3Client(credentialsProvider);
    }

    public void setAmazonS3Client(CognitoCachingCredentialsProvider amazonS3Client) {
        s3 = new AmazonS3Client(amazonS3Client);
        s3.setRegion(Region.getRegion(Regions.US_EAST_1));
    }

    public void setTransferUtility() {
        transferUtility = TransferUtility.builder().defaultBucket("omessenger-userfiles-mobilehub-792948277/public").s3Client(s3).context(this).build();
    }

    @Override
    public void onClick(View v) {
        TransferObserver observer;
        if (v.getId() == R.id.upload_btn) {
            observer = transferUtility.upload("POC-test", file);
            transferObserverListener(observer);
        }
        if (v.getId() == R.id.download_btn) {
            observer = transferUtility.download("POC-test", downloaded);
            transferObserverListener(observer);
        }

    }

    public void transferObserverListener(TransferObserver observer) {
        observer.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state)
                    Log.d("MainActivity", "Done");
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int) percentDonef;

                Log.d("MainActivity", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("MainActivity", "onError: ", ex);
            }
        });
    }
}
