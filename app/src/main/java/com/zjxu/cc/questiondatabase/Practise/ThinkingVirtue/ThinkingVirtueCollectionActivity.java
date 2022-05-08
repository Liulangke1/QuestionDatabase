package com.zjxu.cc.questiondatabase.Practise.ThinkingVirtue;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.zjxu.cc.questiondatabase.App;
import com.zjxu.cc.questiondatabase.CommonActivity;
import com.zjxu.cc.questiondatabase.Question;
import com.zjxu.cc.questiondatabase.R;
import com.zjxu.cc.questiondatabase.bean.MyDatabaseOpenHelper;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O_MR1)
public class ThinkingVirtueCollectionActivity extends Activity implements SeekBar.OnSeekBarChangeListener {

    public final String TAG = "Answer";
    private int count;
    private int questionNumber = 1;
    private int current=0;
    private int mode = 0; // 单选0 多选1 判断2
    private boolean wrongMode;//标志变量，判断是否进入错题模式

    private int progress=0;     //当前进度条的值

    public TextView tv_thinkingvirtue_question;
    public String thinkingvirtue_nowAnswer = "";

    //手势控制
    private static final int FLING_MIN_DISTANCE = 120;
    private static final int FLING_MIN_VELOCITY = 200;
    private GestureDetector mGestureDetector;
    private View.OnTouchListener mOnTouchListener;
    private GestureDetector.SimpleOnGestureListener mySimpleOnGestureListener = new GestureDetector.SimpleOnGestureListener(){

        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            // TODO Auto-generated method stub
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                queryCallback_Next(current);
                test_seekbar.setProgress(current);
            }
            if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                queryCallback_Previous(current);
                test_seekbar.setProgress(current);
            }
            return false;
        }
    };

    //控件
    public RadioButton[] thinkingvirtue_radioButtons = new RadioButton[4];
    public CheckBox thinkingvirtue_checkBox1;
    public CheckBox thinkingvirtue_checkBox2;
    public CheckBox thinkingvirtue_checkBox3;
    public CheckBox thinkingvirtue_checkBox4;
    private boolean[] thinkingvirtue_checkedArray = new boolean[]{false, false, false, false};

    private ImageButton btn_thinkingvirtue_answer;
    private TextView questionNum;
    private TextView tv_thinkingvirtue_explaination;
    private TextView user_thinkingvirtue_answer;
    private RadioGroup radioGroup_thinkingvirtue;

    private SeekBar test_seekbar;

    CommonActivity commonActivity=new CommonActivity();
    MyDatabaseOpenHelper mMyDatabaseOpenHelper=new MyDatabaseOpenHelper(this);
    final List<Question> thinkingvirtue_list=mMyDatabaseOpenHelper.getCollectionQuestion("ThinkingVirtue",this);
    Question question_thinkingvirtue = thinkingvirtue_list.get(0);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practises_collection);

        mGestureDetector=new GestureDetector(mySimpleOnGestureListener);
        mOnTouchListener= (arg0, arg1) -> {
            if (mGestureDetector.onTouchEvent(arg1)) {
                return true;
            }
            return false;
        };

        count = thinkingvirtue_list.size();
        mode = 0;
        wrongMode = false;//默认情况

        tv_thinkingvirtue_question = findViewById(R.id.Question_Collection);
        questionNum = findViewById(R.id.QuestionNum_Collection);
        thinkingvirtue_radioButtons[0] = findViewById(R.id.answerA_Collection);
        thinkingvirtue_radioButtons[1] = findViewById(R.id.answerB_Collection);
        thinkingvirtue_radioButtons[2] = findViewById(R.id.answerC_Collection);
        thinkingvirtue_radioButtons[3] = findViewById(R.id.answerD_Collection);
        thinkingvirtue_checkBox1 = findViewById(R.id.c1_Collection);
        thinkingvirtue_checkBox2 = findViewById(R.id.c2_Collection);
        thinkingvirtue_checkBox3 = findViewById(R.id.c3_Collection);
        thinkingvirtue_checkBox4 = findViewById(R.id.c4_Collection);

        btn_thinkingvirtue_answer = findViewById(R.id.btn_answer_Collection);
        btn_thinkingvirtue_answer.setVisibility(View.GONE);

        tv_thinkingvirtue_explaination = findViewById(R.id.explaination_Collection);
        user_thinkingvirtue_answer = findViewById(R.id.text_answer_Collection);
        radioGroup_thinkingvirtue = findViewById(R.id.radioGroup_Collection);
        test_seekbar=findViewById(R.id.test_seekbar_Collection);

        //为控件赋值
        thinkingvirtue_nowAnswer = question_thinkingvirtue.explaination;
        questionNum.setText("当前第1道题");
        tv_thinkingvirtue_question.setText(question_thinkingvirtue.question);
        thinkingvirtue_radioButtons[0].setText(question_thinkingvirtue.answerA);
        thinkingvirtue_radioButtons[1].setText(question_thinkingvirtue.answerB);
        thinkingvirtue_radioButtons[2].setText(question_thinkingvirtue.answerC);
        thinkingvirtue_radioButtons[3].setText(question_thinkingvirtue.answerD);
        thinkingvirtue_checkBox1.setVisibility(View.GONE);
        thinkingvirtue_checkBox2.setVisibility(View.GONE);
        thinkingvirtue_checkBox3.setVisibility(View.GONE);
        thinkingvirtue_checkBox4.setVisibility(View.GONE);

        test_seekbar.setMax(thinkingvirtue_list.size());
        test_seekbar.setOnSeekBarChangeListener(this);


        //查看答案
        btn_thinkingvirtue_answer.setOnClickListener(view -> {
            tv_thinkingvirtue_explaination.setText(thinkingvirtue_nowAnswer);
            tv_thinkingvirtue_explaination.setVisibility(View.VISIBLE);
            Log.i(TAG, "BTN_Answer : " + thinkingvirtue_nowAnswer);
        });

        radioGroup_thinkingvirtue.setOnCheckedChangeListener((radioGroup1, checkedId) -> {
            for (int i = 0; i < 4; i++) {
                if ( thinkingvirtue_radioButtons[i].isChecked() == true) {
                    thinkingvirtue_list.get(current).selectedAnswer = i;
                    if(thinkingvirtue_list.get(current).selectedAnswer!=thinkingvirtue_list.get(current).answer){
                        Toast.makeText(this, "抱歉，你答错了噢", Toast.LENGTH_SHORT).show();
                    }
                    tv_thinkingvirtue_explaination.setText(thinkingvirtue_list.get(current).explaination);
                    tv_thinkingvirtue_explaination.setVisibility(View.VISIBLE);
                    break;
                }
            }
            Log.i("Test", "checkAnswer: " + thinkingvirtue_list.get(current).question + " " + thinkingvirtue_list.get(current).selectedAnswer+"     "+thinkingvirtue_list.get(current).answer);
        });
        thinkingvirtue_checkBox1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            thinkingvirtue_checkedArray[0] = isChecked;
            thinkingvirtue_list.get(current).selectedAnswer = commonActivity.JudgeCheckButton( thinkingvirtue_checkedArray);
            Log.i(TAG, "C4: T" + " " + thinkingvirtue_checkedArray[0] + " " +  thinkingvirtue_list.get(current).selectedAnswer);
        });
        thinkingvirtue_checkBox2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            thinkingvirtue_checkedArray[1] = isChecked;
            thinkingvirtue_list.get(current).selectedAnswer = commonActivity.JudgeCheckButton( thinkingvirtue_checkedArray);
            Log.i(TAG, "C4: T" + " " + thinkingvirtue_checkedArray[1] + " " +  thinkingvirtue_list.get(current).selectedAnswer);
        });
        thinkingvirtue_checkBox3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            thinkingvirtue_checkedArray[2] = isChecked;
            thinkingvirtue_list.get(current).selectedAnswer = commonActivity.JudgeCheckButton( thinkingvirtue_checkedArray);
            Log.i(TAG, "C4: T" + " " + thinkingvirtue_checkedArray[2] + " " +  thinkingvirtue_list.get(current).selectedAnswer);
        });
        thinkingvirtue_checkBox4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            thinkingvirtue_checkedArray[3] = isChecked;
            thinkingvirtue_list.get(current).selectedAnswer = commonActivity.JudgeCheckButton( thinkingvirtue_checkedArray);
            Log.i(TAG, "C4: T" + " " + thinkingvirtue_checkedArray[3] + " " +  thinkingvirtue_list.get(current).selectedAnswer);
        });
        Log.i(TAG, "Judge: " + commonActivity.JudgeCheckButton(thinkingvirtue_checkedArray));
    }

    public void queryCallback_Next(int number) {
        current=number;
        tv_thinkingvirtue_explaination.setVisibility(View.GONE);
        if (current < count - 1) {//若当前题目不为最后一题，点击next按钮跳转到下一题；否则不响应
            current++;
            questionNumber = current + 1;
            Question question = thinkingvirtue_list.get(current);
            mode = question.mode;

            thinkingvirtue_nowAnswer = question.explaination;
            if (mode == 0) {
                commonActivity.UpdateSimpleQuestion(questionNum,questionNumber,tv_thinkingvirtue_question,thinkingvirtue_radioButtons,
                        thinkingvirtue_checkBox1,thinkingvirtue_checkBox2,thinkingvirtue_checkBox3,thinkingvirtue_checkBox4,
                        question,tv_thinkingvirtue_explaination,user_thinkingvirtue_answer,wrongMode);

                // 若之前已经选择过，则应记录选择
                radioGroup_thinkingvirtue.clearCheck();
                if (question.selectedAnswer != -1) {
                    thinkingvirtue_radioButtons[question.selectedAnswer].setChecked(true);
                }

            } else if (mode == 1) {
                commonActivity.UpdateJudgeQuestion(tv_thinkingvirtue_question,questionNum,tv_thinkingvirtue_explaination,user_thinkingvirtue_answer,thinkingvirtue_radioButtons,
                        thinkingvirtue_checkBox1,thinkingvirtue_checkBox2,thinkingvirtue_checkBox3,thinkingvirtue_checkBox4,
                        question,questionNumber,wrongMode);
                //若之前已经选择过，则应记录选择
                radioGroup_thinkingvirtue.clearCheck();
                if (question.selectedAnswer != -1) {
                    thinkingvirtue_radioButtons[question.selectedAnswer].setChecked(true);
                }
            } else if (mode == 2) {
                btn_thinkingvirtue_answer.setVisibility(View.VISIBLE);
                commonActivity.UpdateMutiChoiceQuestion(tv_thinkingvirtue_question,questionNum,tv_thinkingvirtue_explaination,user_thinkingvirtue_answer,
                        thinkingvirtue_radioButtons,thinkingvirtue_checkBox1,thinkingvirtue_checkBox2,thinkingvirtue_checkBox3,thinkingvirtue_checkBox4,
                        question,questionNumber,wrongMode,thinkingvirtue_checkedArray);

                //若之前已经选择过，则应记录选择
                thinkingvirtue_checkBox1.setChecked(false);
                thinkingvirtue_checkBox2.setChecked(false);
                thinkingvirtue_checkBox3.setChecked(false);
                thinkingvirtue_checkBox4.setChecked(false);
                if (question.selectedAnswer != -1) {
                    commonActivity.SetCheckBox(question.selectedAnswer,thinkingvirtue_checkBox1,thinkingvirtue_checkBox2,thinkingvirtue_checkBox3,thinkingvirtue_checkBox4);
                }
            }
        }

        //错题模式的最后一题
        else if (current == count - 1 && wrongMode == true) {
            new AlertDialog.Builder(ThinkingVirtueCollectionActivity.this)
                    .setTitle("提示")
                    .setMessage("已经到达最后一题，是否退出？")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ThinkingVirtueCollectionActivity.this.finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();

        } else {
            //当前题目为最后一题时，告知用户作答正确的数量和作答错误的数量，并询问用户是否要查看错题
            final List<Integer> wrongList = commonActivity.checkAnswer(thinkingvirtue_list);

            //作对所有题目
            if (wrongList.size() == 0) {
                new AlertDialog.Builder(ThinkingVirtueCollectionActivity.this)
                        .setTitle("提示")
                        .setMessage("恭喜你全部回答正确！")
                        .setPositiveButton("确定", (dialogInterface, i) -> {
                            ThinkingVirtueCollectionActivity.this.finish();
                        }).show();

            } else
                new AlertDialog.Builder(ThinkingVirtueCollectionActivity.this)
                        .setTitle("提示")
                        .setMessage("您答对了" + (thinkingvirtue_list.size() - wrongList.size()) +
                                "道题目；答错了" + wrongList.size() + "道题目。是否查看错题？")
                        .setPositiveButton("确定", (dialogInterface, which) -> {
                            //判断进入错题模式
                            questionNum.setText("当前第" + 1 + "道题");
                            wrongMode = true;
                            List<Question> newList = new ArrayList<>();
                            //将错误题目复制到newList中
                            for (int i = 0; i < wrongList.size(); i++) {
                                newList.add(thinkingvirtue_list.get(wrongList.get(i)));
                            }
                            //将原来的list清空
                            thinkingvirtue_list.clear();
                            //将错误题目添加到原来的list中
                            for (int i = 0; i < newList.size(); i++) {
                                thinkingvirtue_list.add(newList.get(i));
                            }
                            int wrong_current = 0;
                            count = thinkingvirtue_list.size();
                            //更新显示时的内容
                            Question q12 = thinkingvirtue_list.get(wrong_current);
                            tv_thinkingvirtue_explaination.setText(q12.explaination);
                            //显示解析
                            tv_thinkingvirtue_explaination.setVisibility(View.VISIBLE);
                            if (q12.mode == 0) {
                                Log.i(TAG, "Mode: 0");
                                user_thinkingvirtue_answer.setText(commonActivity.getUserAnswer(q12.selectedAnswer));
                                user_thinkingvirtue_answer.setVisibility(View.VISIBLE);
                                tv_thinkingvirtue_question.setText(q12.question);
                                thinkingvirtue_radioButtons[0].setText(q12.answerA);
                                thinkingvirtue_radioButtons[1].setText(q12.answerB);
                                thinkingvirtue_radioButtons[2].setText(q12.answerC);
                                thinkingvirtue_radioButtons[3].setText(q12.answerD);
                                thinkingvirtue_radioButtons[0].setVisibility(View.VISIBLE);
                                thinkingvirtue_radioButtons[1].setVisibility(View.VISIBLE);
                                thinkingvirtue_radioButtons[2].setVisibility(View.VISIBLE);
                                thinkingvirtue_radioButtons[3].setVisibility(View.VISIBLE);

                                thinkingvirtue_checkBox1.setVisibility(View.GONE);
                                thinkingvirtue_checkBox2.setVisibility(View.GONE);
                                thinkingvirtue_checkBox3.setVisibility(View.GONE);
                                thinkingvirtue_checkBox4.setVisibility(View.GONE);

                            } else if (q12.mode == 1) {
                                user_thinkingvirtue_answer.setText(commonActivity.getUserAnswer(q12.selectedAnswer));
                                user_thinkingvirtue_answer.setVisibility(View.VISIBLE);
                                Log.i(TAG, "Mode: 1");
                                tv_thinkingvirtue_question.setText(q12.question);
                                thinkingvirtue_radioButtons[0].setText(q12.answerA);
                                thinkingvirtue_radioButtons[1].setText(q12.answerB);
                                thinkingvirtue_radioButtons[0].setVisibility(View.VISIBLE);
                                thinkingvirtue_radioButtons[1].setVisibility(View.VISIBLE);
                                thinkingvirtue_radioButtons[2].setVisibility(View.GONE);
                                thinkingvirtue_radioButtons[3].setVisibility(View.GONE);
                                thinkingvirtue_checkBox1.setVisibility(View.GONE);
                                thinkingvirtue_checkBox2.setVisibility(View.GONE);
                                thinkingvirtue_checkBox3.setVisibility(View.GONE);
                                thinkingvirtue_checkBox4.setVisibility(View.GONE);
                            } else if (mode == 2) {
                                questionNum.setText("当前第" + questionNumber + "道题");
                                tv_thinkingvirtue_question.setText(q12.question);
                                user_thinkingvirtue_answer.setVisibility(View.GONE);
                                thinkingvirtue_radioButtons[0].setVisibility(View.GONE);
                                thinkingvirtue_radioButtons[1].setVisibility(View.GONE);
                                thinkingvirtue_radioButtons[2].setVisibility(View.GONE);
                                thinkingvirtue_radioButtons[3].setVisibility(View.GONE);

                                thinkingvirtue_checkBox1.setText(q12.answerA);
                                thinkingvirtue_checkBox2.setText(q12.answerB);
                                thinkingvirtue_checkBox3.setText(q12.answerC);
                                thinkingvirtue_checkBox4.setText(q12.answerD);

                                thinkingvirtue_checkBox1.setVisibility(View.VISIBLE);
                                thinkingvirtue_checkBox2.setVisibility(View.VISIBLE);
                                thinkingvirtue_checkBox3.setVisibility(View.VISIBLE);
                                thinkingvirtue_checkBox4.setVisibility(View.VISIBLE);

                            }

                        })
                        .setNegativeButton("取消", (dialogInterface, which) -> {
                            //点击取消时，关闭当前activity
                            ThinkingVirtueCollectionActivity.this.finish();
                        }).show();
        }
    }

    public void queryCallback_Previous(int number){
        current=number;
        tv_thinkingvirtue_explaination.setVisibility(View.GONE);
        if (current > 0)//若当前题目不为第一题，点击previous按钮跳转到上一题；否则不响应
        {
            current--;
            questionNumber = current + 1;
            Question q1 = thinkingvirtue_list.get(current);
            tv_thinkingvirtue_question.setText(q1.question);
            questionNum.setText("当前第" + questionNumber + "道题");

            thinkingvirtue_nowAnswer = q1.explaination;

            if (q1.mode == 0) {
                Log.i(TAG, "LastMode: 0");
                thinkingvirtue_radioButtons[0].setText(q1.answerA);
                thinkingvirtue_radioButtons[1].setText(q1.answerB);
                thinkingvirtue_radioButtons[2].setText(q1.answerC);
                thinkingvirtue_radioButtons[3].setText(q1.answerD);
                thinkingvirtue_radioButtons[0].setVisibility(View.VISIBLE);
                thinkingvirtue_radioButtons[1].setVisibility(View.VISIBLE);
                thinkingvirtue_radioButtons[2].setVisibility(View.VISIBLE);
                thinkingvirtue_radioButtons[3].setVisibility(View.VISIBLE);
                thinkingvirtue_checkBox1.setVisibility(View.GONE);
                thinkingvirtue_checkBox2.setVisibility(View.GONE);
                thinkingvirtue_checkBox3.setVisibility(View.GONE);
                thinkingvirtue_checkBox4.setVisibility(View.GONE);
                if (wrongMode) {
                    tv_thinkingvirtue_explaination.setText(q1.explaination);
                    tv_thinkingvirtue_explaination.setVisibility(View.VISIBLE);
                    user_thinkingvirtue_answer.setText(commonActivity.getUserAnswer(q1.selectedAnswer));
                    user_thinkingvirtue_answer.setVisibility(View.VISIBLE);
                    Log.i(TAG, "last" + " " +question_thinkingvirtue.explaination);
                }

                //若之前已经选择过，则应记录选择
                radioGroup_thinkingvirtue.clearCheck();
                if (q1.selectedAnswer != -1) {
                    thinkingvirtue_radioButtons[q1.selectedAnswer].setChecked(true);
                }

            } else if (q1.mode == 1) {
                Log.i(TAG, "LastMode: 1");
                thinkingvirtue_radioButtons[0].setText(q1.answerA);
                thinkingvirtue_radioButtons[1].setText(q1.answerB);
                thinkingvirtue_radioButtons[2].setText(q1.answerB);
                thinkingvirtue_radioButtons[3].setText(q1.answerB);
                thinkingvirtue_radioButtons[0].setVisibility(View.VISIBLE);
                thinkingvirtue_radioButtons[1].setVisibility(View.VISIBLE);
                thinkingvirtue_radioButtons[2].setVisibility(View.GONE);
                thinkingvirtue_radioButtons[3].setVisibility(View.GONE);
                thinkingvirtue_checkBox1.setVisibility(View.GONE);
                thinkingvirtue_checkBox2.setVisibility(View.GONE);
                thinkingvirtue_checkBox3.setVisibility(View.GONE);
                thinkingvirtue_checkBox4.setVisibility(View.GONE);
                if (wrongMode) {
                    tv_thinkingvirtue_explaination.setText(q1.explaination);
                    tv_thinkingvirtue_explaination.setVisibility(View.VISIBLE);
                    user_thinkingvirtue_answer.setText(commonActivity.getUserAnswer(q1.selectedAnswer));
                    user_thinkingvirtue_answer.setVisibility(View.VISIBLE);
                    Log.i(TAG, "last" + " " + question_thinkingvirtue.explaination);
                }

                //若之前已经选择过，则应记录选择
                radioGroup_thinkingvirtue.clearCheck();
                if (q1.selectedAnswer != -1) {
                    thinkingvirtue_radioButtons[q1.selectedAnswer].setChecked(true);
                }

            } else if (mode == 2) {
                Log.i(TAG, "Judge: " + commonActivity.JudgeCheckButton(thinkingvirtue_checkedArray));
                questionNum.setText("当前第" + questionNumber + "道题");
                tv_thinkingvirtue_question.setText(q1.question);
                user_thinkingvirtue_answer.setVisibility(View.GONE);
                thinkingvirtue_radioButtons[0].setVisibility(View.GONE);
                thinkingvirtue_radioButtons[1].setVisibility(View.GONE);
                thinkingvirtue_radioButtons[2].setVisibility(View.GONE);
                thinkingvirtue_radioButtons[3].setVisibility(View.GONE);

                thinkingvirtue_checkBox1.setText(q1.answerA);
                thinkingvirtue_checkBox2.setText(q1.answerB);
                thinkingvirtue_checkBox3.setText(q1.answerC);
                thinkingvirtue_checkBox4.setText(q1.answerD);

                thinkingvirtue_checkBox1.setVisibility(View.VISIBLE);
                thinkingvirtue_checkBox2.setVisibility(View.VISIBLE);
                thinkingvirtue_checkBox3.setVisibility(View.VISIBLE);
                thinkingvirtue_checkBox4.setVisibility(View.VISIBLE);
                if (wrongMode) {
                    tv_thinkingvirtue_explaination.setText(q1.explaination);
                    tv_thinkingvirtue_explaination.setVisibility(View.VISIBLE);
                    user_thinkingvirtue_answer.setText(commonActivity.getUserAnswer(q1.selectedAnswer));
                    user_thinkingvirtue_answer.setVisibility(View.VISIBLE);
                    Log.i(TAG, "next" + " " + q1.explaination);
                }

                if (q1.selectedAnswer != -1) {
                    thinkingvirtue_checkBox1.setChecked(false);
                    thinkingvirtue_checkBox2.setChecked(false);
                    thinkingvirtue_checkBox3.setChecked(false);
                    thinkingvirtue_checkBox4.setChecked(false);
                    commonActivity.SetCheckBox(q1.selectedAnswer,thinkingvirtue_checkBox1,thinkingvirtue_checkBox2,thinkingvirtue_checkBox3,thinkingvirtue_checkBox4);

                }
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mGestureDetector.onTouchEvent(event)) {
            event.setAction(MotionEvent.ACTION_CANCEL);
        }
        return super.dispatchTouchEvent(event);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        this.progress=progress;
        queryCallback_Next(progress-1);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        test_seekbar.setProgress(current);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        current=progress;
        Log.i(TAG,"当前页为："+progress);
    }
}
