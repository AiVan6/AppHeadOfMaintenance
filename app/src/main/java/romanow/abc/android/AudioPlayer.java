package romanow.abc.android;

import android.media.MediaPlayer;
import android.widget.ImageView;

public class AudioPlayer {

    private ImageView LoadVoice;
    private MediaPlayer mediaPlayer;
    private MainActivity base;
    private boolean playing;
    public ImageView getCurrentImageView(){
        return LoadVoice;
    }
    public AudioPlayer(ImageView view0, MainActivity base0) {
        LoadVoice = view0;
        base = base0;
    }
    public void setImageView(ImageView vv){
        LoadVoice = vv;

        System.out.println("LoadVoice:"+LoadVoice);
        if (LoadVoice!=null)
            LoadVoice.setImageResource(R.drawable.play_on);
    }
    public boolean isPlaying(){
        return playing;
    }
    public void releasePlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void playStart(String fname) {
        if (playing)
            playStop();
        try {
            releasePlayer();
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fname);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    playStop();
                }
            });
            mediaPlayer.prepare();
            mediaPlayer.start();
            if (LoadVoice!=null)
                LoadVoice.setImageResource(R.drawable.play_off);
            playing=true;
        } catch (Exception e) { base.popupInfo(e.toString()); }
    }
    public void playStop() {
        if (!playing)
            return;
        //releasePlayer();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer=null;
        }
        if (LoadVoice!=null)
            LoadVoice.setImageResource(R.drawable.play_on);
        playing=false;
    }
}
