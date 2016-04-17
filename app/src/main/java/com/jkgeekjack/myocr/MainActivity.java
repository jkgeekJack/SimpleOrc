package com.jkgeekjack.myocr;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {
    private static Map<Bitmap, String> trainMap =new HashMap<Bitmap, String>();
    private TextView tv;
    private ImageView iv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv= (TextView) findViewById(R.id.tv);
        iv= (ImageView) findViewById(R.id.iv);
        Bitmap code = null;
        try {
            InputStream inputStream = getResources().getAssets().open("code.png");
            code = BitmapFactory.decodeStream(inputStream);
            //这里可以将bitmap换成自己获取的验证码，我这里用的是本地的
        } catch (IOException e) {
            e.printStackTrace();
        }
        iv.setImageBitmap(code);
        String text = getAllOcr(code);
        tv.setText(text);
    }
    private String getAllOcr(Bitmap code) {
        Bitmap aftercode =removeBackGround(code);
        List<Bitmap>listImg=split(aftercode);
        Map<Bitmap,String>map=loadTrainData();
        String result = "";
        for (Bitmap bi : listImg) {
            result += getSingleCharOcr(bi, map);

        }
        return result;
    }

    private String getSingleCharOcr(Bitmap img, Map<Bitmap, String> map) {
        String result = "#";
        int width = img.getWidth();
        int height =img.getHeight();
        int min = width * height;
        for (Bitmap bi : map.keySet()) {
            int count = 0;

            if (Math.abs(bi.getWidth()-width) > 2)
                continue;
            int widthmin = width < bi.getWidth() ? width : bi.getWidth();
            int heightmin = height < bi.getHeight() ? height : bi.getHeight();
            Label1: for (int x = 0; x < widthmin; ++x) {
                for (int y = 0; y < heightmin; ++y) {
                    if (isBlack(img.getPixel(x, y)) != isBlack(bi.getPixel(x, y))) {
                        count++;
                        if (count >= min)
                            break Label1;
                    }
                }
            }

            if (count < min) {
                min = count;
                result = map.get(bi);
            }
        }
        return result;
    }

    private List<Bitmap> split(Bitmap aftercode) {
        List<Bitmap> subImgs = new ArrayList<Bitmap>();
        int width = aftercode.getWidth()/4;
        int height = aftercode.getHeight();
        subImgs.add(Bitmap.createBitmap(aftercode,0,0,width,height));
        subImgs.add(Bitmap.createBitmap(aftercode,width,0,width,height));
        subImgs.add(Bitmap.createBitmap(aftercode,width*2,0,width,height));
        subImgs.add(Bitmap.createBitmap(aftercode,width*3,0,width,height));
        return subImgs;
    }

    private Map<Bitmap,String> loadTrainData() {
        try {
            String[] picsptah=getAssets().list("trainimg");
//            Log.e("size",picsptah.length+"");
            InputStream inputStream;
            Bitmap bitmap;
            for (String picpath:picsptah){
                inputStream=getResources().getAssets().open("trainimg/"+picpath);
                bitmap = BitmapFactory.decodeStream(inputStream);
//                piclist.add(bitmap);
                if (bitmap!=null){
                    trainMap.put(bitmap,picpath.charAt(0)+"");
                }

//                Log.e("pic",picpath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return trainMap;
    }

    private Bitmap removeBackGround(Bitmap code) {
        Bitmap cutcode=Bitmap.createBitmap(code,5,1,code.getWidth()-5,code.getHeight()-2);
        cutcode=Bitmap.createBitmap(cutcode,0,0,50,cutcode.getHeight());
        int width=cutcode.getWidth();
        int height=cutcode.getHeight();
        for (int x=0;x<width;x++)
            for (int y=0;y<height;y++)
            {
                if(isBlue(cutcode.getPixel(x,y))==1){
                    cutcode.setPixel(x,y,Color.BLACK);
                }else {
                    cutcode.setPixel(x,y,Color.WHITE);
                }
            }
        return cutcode;
    }

    private int isBlue(int pixel) {
        Log.e("Blue",Color.red(pixel)+Color.red(pixel)+Color.red(pixel)+"");
        if (Color.red(pixel)+Color.red(pixel)+Color.red(pixel)==0){
            return 1;
        }else{
            return 0;
        }
    }
    private int isBlack(int pixel) {
        if (Color.red(pixel)+Color.red(pixel)+Color.red(pixel)<100){
            return 1;
        }else{
            return 0;
        }
    }
}
