package com.example.demo_for_android_kotlin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SuperAudioManager {
    public static final String TAG = "MYTAG";
    public final int sampleRateInHz = 16000;//sampleRate
    public final int channelInMono = AudioFormat.CHANNEL_IN_MONO;//input channel
    public final int channelOutMono = AudioFormat.CHANNEL_OUT_MONO;//output channel
    public final int encodingPcm16bit = AudioFormat.ENCODING_PCM_16BIT;//bit
    public final int mReadMinBufferSize;
    private final int mWriteMinBufferSize;
    private AudioRecord mAudioRecord;
    private AudioTrack mAudioTrack;
    private boolean isRecording;
    private Context mContext;

    private ExecutorService mExecutorService;
    private SuperAudioManager.Callback mCallback;

    @SuppressLint("MissingPermission")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public SuperAudioManager(Context context) {
        mContext = context;
        //Get the smallest input cache.
        mReadMinBufferSize = AudioRecord.getMinBufferSize(sampleRateInHz, channelInMono, encodingPcm16bit);
        //Initialize the minimum cache for audio recording.
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRateInHz, channelInMono, encodingPcm16bit, mReadMinBufferSize);

        //Get the smallest output cache.
        mWriteMinBufferSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelOutMono, encodingPcm16bit);
        //Get Media Format.
        AudioFormat audioFormat = new AudioFormat.Builder().setSampleRate(sampleRateInHz).setEncoding(encodingPcm16bit).setChannelMask(channelOutMono).build();
        //Get Media Properties.
        AudioAttributes audioAttributes = new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
        //Create a playback media streaming object.
        mAudioTrack = new AudioTrack(audioAttributes, audioFormat, mWriteMinBufferSize, AudioTrack.MODE_STREAM, android.media.AudioManager.AUDIO_SESSION_ID_GENERATE);
    }

    public void startRecord(final String filepath, SuperAudioManager.Callback callback) {
        Log.v(TAG, "startRecord startRecord...");
        Log.v(TAG, "filePath:" + filepath);
        Log.v(TAG, "minBufferSize:" + mReadMinBufferSize);
        //If recording is in progress, it returns.
        if(isRecording){
            return;
        }
        this.mCallback = callback;
        //start record
        mAudioRecord.startRecording();
        isRecording = true;
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(new File(filepath));
                    //Declare a cached buffer.
                    byte[] buffer = new byte[mReadMinBufferSize];
                    while (isRecording) {
                        //Obtain the length of bytes read.
                        int read = mAudioRecord.read(buffer, 0, mReadMinBufferSize);
                       // Log.v(TAG, "recording...");
                        if (read > 0) {
                            if (SuperAudioManager.this.mCallback != null && isRecording) {
                                SuperAudioManager.this.mCallback.run(buffer, read);
                            }
                        }
                        // If there are no errors when reading audio data, write the data to a file
                        if (AudioRecord.ERROR_INVALID_OPERATION != read) {

                            fileOutputStream.write(buffer);
                        }

                    }
                    //Close stream after stopping recording.
                    fileOutputStream.close();
                    SuperAudioManager.this.mCallback = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void stopRecord() {
        Log.v(TAG, "stopRecord startRecord...");
        // stop recording.
        if (mAudioRecord != null && isRecording) {
            mAudioRecord.stop();
            //mAudioRecord.release();Note: This object cannot be used again. If it is recorded again, an error will be reported
            isRecording = false;

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void play(final String filepath) {
        mAudioTrack.play();
        if(mExecutorService == null){
            mExecutorService = Executors.newSingleThreadExecutor();
        }
        mExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    //Create a file input stream.
                    FileInputStream fileInputStream = new FileInputStream(filepath);
                    //Create a buffer cache.
                    byte[] tempBuffer = new byte[mWriteMinBufferSize];
                    //Read in file stream.

                    while (fileInputStream.available() > 0) {
                        int readCount = fileInputStream.read(tempBuffer);
                        //If there is a problem with the data read, skip this loop.
                        if (readCount == AudioTrack.ERROR_INVALID_OPERATION || readCount == AudioTrack.ERROR_BAD_VALUE) {
                            continue;
                        }
                        //Input the read file stream into the playback stream for playback.
                        if (readCount != 0 && readCount != -1) {
                            mAudioTrack.write(tempBuffer, 0, readCount);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void destory(){
        if(mAudioRecord != null){
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if(mAudioTrack != null){
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }

    public interface Callback {
        void run(byte[] var1, int var2);
    }
}
