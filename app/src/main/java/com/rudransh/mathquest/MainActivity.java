package com.rudransh.mathquest;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.speech.tts.TextToSpeech;
import android.view.*;
import android.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends Activity implements TextToSpeech.OnInitListener {
    private LinearLayout root, body; private TextView coinsView, levelView, streakView;
    private final Random rng = new Random(); private TextToSpeech tts;
    private int answer, coins, streak, best, attempts, correct, level; private String topic = "Mixed Adventure";
    private android.content.SharedPreferences prefs;
    private final int purple=Color.rgb(108,77,255), navy=Color.rgb(23,19,59), mint=Color.rgb(55,205,164), orange=Color.rgb(255,163,72);

    @Override public void onCreate(Bundle b){super.onCreate(b); prefs=getSharedPreferences("quest",0); load(); tts=new TextToSpeech(this,this); home();}
    private void load(){coins=prefs.getInt("coins",0);streak=prefs.getInt("streak",0);best=prefs.getInt("best",0);attempts=prefs.getInt("attempts",0);correct=prefs.getInt("correct",0);level=prefs.getInt("level",1);}
    private void save(){prefs.edit().putInt("coins",coins).putInt("streak",streak).putInt("best",best).putInt("attempts",attempts).putInt("correct",correct).putInt("level",level).apply();}
    private TextView text(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setGravity(Gravity.CENTER);v.setPadding(16,12,16,12);return v;}
    private GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(radius);return d;}
    private Button button(String s,int color){Button b=new Button(this);b.setText(s);b.setTextSize(18);b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setBackground(bg(color,28));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.setMargins(12,8,12,8);b.setLayoutParams(p);b.setPadding(10,18,10,18);return b;}
    private void frame(String title){ScrollView sc=new ScrollView(this);root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(18,24,18,30);root.setBackgroundColor(Color.rgb(244,241,255));sc.addView(root);TextView head=text("🚀  "+title,27,Color.WHITE);head.setGravity(Gravity.START|Gravity.CENTER_VERTICAL);head.setBackground(bg(navy,30));head.setPadding(24,28,24,28);root.addView(head,new LinearLayout.LayoutParams(-1,-2));LinearLayout stats=new LinearLayout(this);stats.setGravity(Gravity.CENTER);coinsView=text("🪙 "+coins,16,navy);levelView=text("⭐ Level "+level,16,navy);streakView=text("🔥 "+streak,16,navy);stats.addView(coinsView,new LinearLayout.LayoutParams(0,-2,1));stats.addView(levelView,new LinearLayout.LayoutParams(0,-2,1));stats.addView(streakView,new LinearLayout.LayoutParams(0,-2,1));root.addView(stats);body=new LinearLayout(this);body.setOrientation(LinearLayout.VERTICAL);root.addView(body);setContentView(sc);}
    private void home(){frame("Rudransh's Math Quest");body.addView(text("Hello, Rudransh! 👋\nChoose your next mission",23,navy));String[][] cards={{"🌳 Number Forest","Numbers & place value"},{"🏝️ Addition Island","Add and carry"},{"🏔️ Subtraction Mountain","Subtract and borrow"},{"🌌 Times-Table Galaxy","2, 3, 4, 5 & 10 tables"},{"🧩 Mixed Adventure","A surprise mix"},{"🏆 Daily 10","Ten-question challenge"}};int[] cs={mint,orange,purple,Color.rgb(34,139,230),Color.rgb(236,81,142),Color.rgb(255,184,0)};for(int i=0;i<cards.length;i++){Button b=button(cards[i][0]+"\n"+cards[i][1],cs[i]);final String t=cards[i][0].substring(3);b.setOnClickListener(v->{topic=t;question();});body.addView(b);}Button parent=button("🔒 Parent Dashboard",navy);parent.setOnClickListener(v->parentPin());body.addView(parent);}
    private void question(){frame(topic);TextView q=text("",34,navy);q.setBackground(bg(Color.WHITE,28));q.setPadding(16,45,16,45);body.addView(q);int max=Math.min(20+level*10,100),a,b;String prompt;
        if(topic.contains("Number")){a=rng.nextInt(max)+1;b=rng.nextInt(max)+1;answer=Math.max(a,b);prompt="Which number is greater?\n"+a+"  or  "+b;}
        else if(topic.contains("Subtraction")){a=rng.nextInt(max)+1;b=rng.nextInt(a+1);answer=a-b;prompt=a+" − "+b+" = ?";}
        else if(topic.contains("Table")){a=new int[]{2,3,4,5,10}[rng.nextInt(5)];b=rng.nextInt(10)+1;answer=a*b;prompt=a+" × "+b+" = ?";}
        else {boolean sub=topic.contains("Mixed")&&rng.nextBoolean();a=rng.nextInt(max)+1;b=rng.nextInt(max)+1;if(sub){int hi=Math.max(a,b),lo=Math.min(a,b);answer=hi-lo;prompt=hi+" − "+lo+" = ?";}else{answer=a+b;prompt=a+" + "+b+" = ?";}}
        q.setText(prompt);Button hear=button("🔊 Read it to me",Color.rgb(34,139,230));hear.setOnClickListener(v->tts.speak(prompt,TextToSpeech.QUEUE_FLUSH,null,"q"));body.addView(hear);ArrayList<Integer> opts=new ArrayList<>();opts.add(answer);while(opts.size()<4){int n=Math.max(0,answer+rng.nextInt(13)-6);if(!opts.contains(n))opts.add(n);}Collections.shuffle(opts);for(int n:opts){Button x=button(String.valueOf(n),purple);x.setOnClickListener(v->check(n));body.addView(x);}Button back=button("← Missions",navy);back.setOnClickListener(v->home());body.addView(back);}
    private void check(int n){attempts++;if(n==answer){correct++;streak++;best=Math.max(best,streak);coins+=10+Math.min(streak,10);level=1+correct/10;save();new AlertDialog.Builder(this).setTitle("Brilliant, Rudransh! 🌟").setMessage("Correct! You earned coins.\nCurrent streak: "+streak).setPositiveButton("Next mission",(d,w)->question()).setCancelable(false).show();}else{streak=0;save();new AlertDialog.Builder(this).setTitle("Good try! 💪").setMessage("Let's think once more. You can do it!").setPositiveButton("Try again",null).show();}}
    private void parentPin(){final EditText pin=new EditText(this);pin.setInputType(2);pin.setHint("Hint: four ones");new AlertDialog.Builder(this).setTitle("Parent check").setMessage("Enter parent PIN").setView(pin).setPositiveButton("Open",(d,w)->{if(pin.getText().toString().equals("1111"))dashboard();else Toast.makeText(this,"Incorrect PIN",Toast.LENGTH_SHORT).show();}).setNegativeButton("Cancel",null).show();}
    private void dashboard(){frame("Parent Dashboard");int accuracy=attempts==0?0:(correct*100/attempts);body.addView(text("Rudransh's learning snapshot",25,navy));body.addView(text("Questions attempted\n"+attempts,22,purple));body.addView(text("Accuracy\n"+accuracy+"%",22,mint));body.addView(text("Best streak\n"+best,22,orange));body.addView(text("Current level\n"+level,22,purple));String guidance=accuracy<60?"Recommended: practise addition and number sense together.":accuracy<80?"Good progress: keep sessions short and regular.":"Excellent progress: try more mixed and table missions.";body.addView(text(guidance,18,navy));Button reset=button("Reset progress",Color.rgb(210,65,75));reset.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Reset all progress?").setMessage("This cannot be undone.").setPositiveButton("Reset",(d,w)->{prefs.edit().clear().apply();load();home();}).setNegativeButton("Cancel",null).show());body.addView(reset);Button back=button("← Back to missions",navy);back.setOnClickListener(v->home());body.addView(back);}
    @Override public void onBackPressed(){home();}
    @Override public void onInit(int s){if(s==TextToSpeech.SUCCESS)tts.setLanguage(Locale.UK);}
    @Override protected void onDestroy(){if(tts!=null)tts.shutdown();super.onDestroy();}
}
