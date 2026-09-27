package com.danandnorlyn.wordclue;

import android.app.*;import android.os.*;import android.graphics.Color;import android.graphics.Typeface;import android.content.*;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;import java.io.*;import java.util.*;

public class MainActivity extends Activity {
  LinearLayout root, body; ArrayList<Word> words=new ArrayList<>(); Random rng=new Random();
  String guesser="DAN", giver="NORLYN"; int target=15, round=1, clue=1, scoreDan=0, scoreNorlyn=0; Word current;
  TextView title, score;
  int purple=Color.rgb(103,80,164), bg=Color.rgb(247,245,250);
  public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(purple); loadWords(); settings();}
  void base(String t){ root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(28,24,28,24); root.setBackgroundColor(bg); title=tv(t,28,true); root.addView(title); body=new LinearLayout(this); body.setOrientation(LinearLayout.VERTICAL); root.addView(body,new LinearLayout.LayoutParams(-1,0,1)); setContentView(root); }
  TextView tv(String s,int z,boolean bold){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(Color.rgb(35,32,40));v.setTypeface(null,bold?Typeface.BOLD:Typeface.NORMAL);v.setPadding(0,8,0,8);return v;}
  Button btn(String s){Button b=new Button(this);b.setText(s);b.setTextSize(16);b.setAllCaps(false);return b;}
  void add(View v){body.addView(v,new LinearLayout.LayoutParams(-1,-2));}
  void loadWords(){try{BufferedReader r=new BufferedReader(new InputStreamReader(getAssets().open("secret_words_revised.txt"),"UTF-8"));String line;Word w=null;while((line=r.readLine())!=null){line=line.trim();if(line.startsWith("--- Secret Word:")){if(w!=null&&w.clues.size()==5)words.add(w);String s=line.substring(16).trim();w=new Word(s); } else if(w!=null && line.matches("[1-5]\\..*")){w.clues.add(line.substring(2).trim());}}if(w!=null&&w.clues.size()==5)words.add(w);r.close();}catch(Exception e){}}
  void settings(){base("WORD CLUE"); add(tv("DAN × NORLYN",20,true)); add(tv("Game Settings",20,true));
    add(tv("Sino ang mauunang GUESSER?",17,false)); LinearLayout rg=new LinearLayout(this); rg.setOrientation(LinearLayout.HORIZONTAL); Button dan=btn("DAN");Button nor=btn("NORLYN");rg.addView(dan,new LinearLayout.LayoutParams(0,-2,1));rg.addView(nor,new LinearLayout.LayoutParams(0,-2,1));add(rg);
    dan.setOnClickListener(v->{guesser="DAN";giver="NORLYN";dan.setEnabled(false);nor.setEnabled(true);});nor.setOnClickListener(v->{guesser="NORLYN";giver="DAN";nor.setEnabled(false);dan.setEnabled(true);});dan.setEnabled(false);
    add(tv("Points to win",17,false)); LinearLayout pts=new LinearLayout(this);pts.setOrientation(LinearLayout.HORIZONTAL);int[] ps={15,20,25,30};for(int p:ps){Button x=btn(p+" pts");x.setOnClickListener(v->{target=Integer.parseInt(((Button)v).getText().toString().split(" ")[0]);});pts.addView(x,new LinearLayout.LayoutParams(0,-2,1));}add(pts);
    add(tv("Word bank: "+words.size()+" secret words • 5 clues each",15,false)); Button start=btn("START GAME");start.setOnClickListener(v->startGame());add(start);
  }
  void startGame(){scoreDan=scoreNorlyn=0;round=1;nextRound();}
  void nextRound(){if(scoreDan>=target||scoreNorlyn>=target){winner();return;} clue=1;current=words.get(rng.nextInt(words.size())); giver=(guesser.equals("DAN")?"NORLYN":"DAN");giverScreen();}
  void scoreBar(){score=tv("DAN  "+scoreDan+"   •   NORLYN  "+scoreNorlyn+"   |   First to "+target,16,true);add(score);}
  void giverScreen(){base("ROUND "+round);scoreBar();add(tv("GIVER: "+giver,18,true));add(tv("SECRET WORD",15,false));TextView secret=tv(current.word,32,true);secret.setTextColor(purple);add(secret);add(tv("Clue "+clue+"  •  "+(6-clue)+" point(s)",18,true));TextView c=tv(current.clues.get(clue-1),22,false);add(c);add(tv("Read this clue aloud to "+guesser+". Do not show the secret word!",14,false));Button pass=btn("PASS TO GUESSER");pass.setOnClickListener(v->guessScreen());add(pass);Button reveal=btn("SKIP TO NEXT CLUE");reveal.setOnClickListener(v->{if(clue<5){clue++;giverScreen();}else{roundEnd(0);}});add(reveal);}
  void guessScreen(){base("ROUND "+round);scoreBar();add(tv("GUESSER: "+guesser,20,true));add(tv("The giver has given Clue "+clue+".",17,false));add(tv("Points available: "+(6-clue),18,true));EditText input=new EditText(this);input.setHint("Type your guess…");input.setSingleLine(true);input.setTextSize(20);add(input);Button check=btn("CHECK GUESS");check.setOnClickListener(v->{String g=input.getText().toString().trim();if(g.equalsIgnoreCase(current.word)){roundEnd(6-clue);}else{input.setText(""); if(clue<5){clue++;giverScreen();}else roundEnd(0);}});add(check);Button no=btn("I GIVE UP / 0 POINTS");no.setOnClickListener(v->roundEnd(0));add(no);input.requestFocus();}
  void roundEnd(int pts){if(pts>0){if(guesser.equals("DAN"))scoreDan+=pts;else scoreNorlyn+=pts;}if(scoreDan>=target||scoreNorlyn>=target){winner();return;}base("ROUND RESULT");scoreBar();add(tv(pts>0?"CORRECT! +"+pts+" POINTS":"NO POINTS",28,true));add(tv("Secret word: "+current.word,22,true));add(tv("Next round: roles will switch.",18,false));Button next=btn("NEXT ROUND");next.setOnClickListener(v->{guesser=guesser.equals("DAN")?"NORLYN":"DAN";round++;nextRound();});add(next);}
  void winner(){base("GAME OVER");scoreBar();String w=scoreDan>=target?"DAN":"NORLYN";add(tv("🏆 "+w+" WINS!",32,true));add(tv("Final score: DAN "+scoreDan+" • NORLYN "+scoreNorlyn,20,false));Button again=btn("PLAY AGAIN");again.setOnClickListener(v->settings());add(again);}
  static class Word{String word;ArrayList<String> clues=new ArrayList<>();Word(String w){word=w;}}
}
