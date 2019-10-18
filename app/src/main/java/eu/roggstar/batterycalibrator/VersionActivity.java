package eu.roggstar.batterycalibrator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class VersionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_version);


        //setTitle("by RoggStar© All rights reserved!");

        final TextView tv_version =  findViewById(R.id.tv_version);
        final ImageView img_rogg = findViewById(R.id.img_rogg);
        final ImageView img_logo = findViewById(R.id.img_logo);
        tv_version.setTextColor(getResources().getColor(R.color.colorPrimary));
        tv_version.setText(BuildConfig.VERSION_NAME + ", " + BuildConfig.VERSION_CODE);

        img_rogg.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View V) {
                        fadeOutAndHideImage(img_rogg);
                        fadeInAndShowImage(img_logo);
                    }
                }
        );
        img_logo.setOnClickListener(
                new Button.OnClickListener() {
                    public void onClick(View V) {
                        fadeOutAndHideImage(img_logo);
                        fadeInAndShowImage(img_rogg);
                    }
                }
        );
    }

    private void fadeInAndShowImage(final ImageView img)
    {
        Animation fadeIn = new AlphaAnimation(0,1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(1000);

        fadeIn.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {
                img.setVisibility(View.VISIBLE);
            }
        });

        img.startAnimation(fadeIn);
    }

    private void fadeOutAndHideImage(final ImageView img)
    {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(1000);

        fadeOut.setAnimationListener(new Animation.AnimationListener()
        {
            public void onAnimationEnd(Animation animation)
            {
                img.setVisibility(View.INVISIBLE);
            }
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
}