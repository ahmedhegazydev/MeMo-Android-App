package com.example.ahmed.memo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.ColorRes;
import android.support.annotation.FloatRange;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringDef;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import db.DBController;
import db.DbController;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    String idForDb = "";
    Context context = null;
    DbController DbController = null;
    String sql = "";
    EditText etWritingNotes = null;
    ImageView ivColorEditText = null;
    LinearLayout linearLayout = null;//the main layout that contains all added sticky notes by user
    String noteColor = "", noteBody, noteDate, noteFontSize;
    View view = null;//the addNewMemo xml view for adding the menu to the alertDialog View
    int id = 0;//by default //for the mainLayout of app
    String selectedStickyId = "";
    RelativeLayout relativeLayout = null;
    int[] colors = new int[]{
            Color.BLUE,
            Color.GREEN,
            Color.CYAN,
            Color.DKGRAY,
            Color.RED,
            Color.GRAY

    };
    String[] strings = new String[]{
            "#FFFFFF",
            "#FFDDEE",
            "#DDFFEE",
            "#AAFFEE",
            "#CCDEDE",
            "#FFDADA",
            "#AAFFEC",
            "#DDDDDD",
            "#EEEEAA",
            "#AFAFAF",
            "#DADAEF"
    };
    ArrayList<HashMap<String, String>> hashMaps = new ArrayList<HashMap<String, String>>();
    HashMap<String, String> hashMap = null;
    LinearLayout llDeletion = null;
    Button btnDelete = null, btnCancel = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

    }

    private void init() {


//        getSupportActionBar().setHomeButtonEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayUseLogoEnabled(true);
//        getSupportActionBar().setLogo(R.drawable.add_new_memo);
//        getSupportActionBar().setDisplayShowTitleEnabled(false); //optional


//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to be clickable
//        getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.add_new_memo));

        setCustomViewForActionBar();


        context = this;
        DbController = new DbController(getApplicationContext());
        linearLayout = (LinearLayout) findViewById(R.id.llExistingNotes);
        relativeLayout = (RelativeLayout) findViewById(R.id.rlMainView);

//        color = getResources().getStringArray(R.array.color_list)[0];//by default
//        createAlert(DbController.getWritableDatabase().getVersion()+"");
//        long l = DbController.insertDataInDb("hfhhf", color, Calendar.getInstance().getTime().toString(), "23232");
//        createToast(l+"");
//
//        DbController.exeQuery("delete from " + db.DbController.tblNotes);//for truncate table
//        createShortToast(DbController.getData("select * from " + db.DbController.tblNotes).toString());

        noteColor = strings[0];//by default the color is white
        addStickyNoteToMainView();
        initLayoutDeletion();

    }

    EditText etSearch = null;
    boolean toggle = true;
    ImageView ivSearch = null;
    LinearLayout view1 = null;
    ImageView ivAddNewNote = null;

    private void setCustomViewForActionBar() {

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);
        final View view = getSupportActionBar().getCustomView();


        ivAddNewNote = (ImageView) view.findViewById(R.id.ivAddOrBack);
        TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        view1 = (LinearLayout) view.findViewById(R.id.llActionBar);
        ivSearch = (ImageView) view.findViewById(R.id.ivSearchFor);
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (toggle) {
                    view1.removeAllViews();
                    EditText editText = new EditText(context);
                    editText.setHint("Search for ..");
                    editText.setTypeface(Typeface.DEFAULT_BOLD);
                    editText.addTextChangedListener(textWatcher);
                    editText.setPadding(3, 3, 3, 3);
                    editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    editText.setTextColor(Color.parseColor("#FFFFFF"));
                    editText.setHintTextColor(Color.parseColor("#FFFFFF"));
                    view1.addView(editText);
                    toggle = false;
                } else {
                    view1.removeAllViews();
                    TextView textView = new TextView(context);
                    textView.setText(" Sticky Notes ");
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                    textView.setTypeface(Typeface.DEFAULT_BOLD);
                    textView.setTextColor(Color.parseColor("#FFFFFF"));
                    textView.setPadding(3, 3, 3, 3);
                    view1.addView(textView);
                    toggle = true;
                }

            }
        });
        ivAddNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    if (toggle2 == true) {
                        addNewMemoItem2();
                        ivAddNewNote.setImageResource(R.mipmap.ic_action_back);
                        id = 1;
                        toggle2 = false;
                        //createShortToast("1");
                    } else {
                        String s = etWritingNotes.getText().toString();
                        if (s.toString().trim().length() != 0) {
                            noteBody = s;
                            //createShortToast(s);
                            saveAndAddSticky();

                        }
                        id = 0;

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                        //createShortToast("2");

                        ivAddNewNote.setImageResource(R.mipmap.add);

                        toggle2 = true;
                    }
                } catch (Exception e) {
                    //createAlert(e.getMessage().toString());
                    //finish();
                }

            }
        });

    }


    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().trim().length() != 0) {
                searchForNotes(s.toString().trim());
            }
        }
    };

    private void searchForNotes(String s) {
        linearLayout.removeAllViews();
        /////////////////////////

        sql = "select * from " + db.DbController.tblNotes;
        hashMaps = DbController.getData(sql);
        if (!hashMaps.isEmpty()) {
            ArrayList<HashMap<String, String>> hashMaps2 = new ArrayList<HashMap<String, String>>();
            for (int i = 0; i < hashMaps.size(); i++) {
                if (hashMaps.get(i).get(db.DbController.noteBody).toString().contains(s)) {
                    hashMaps2.add(hashMaps.get(i));
                }
            }

            for (int i = 0; i < hashMaps2.size(); i++) {
                HashMap<String, String> hashMap = hashMaps2.get(i);
                String id = hashMap.get(DbController.noteId);
                String text = hashMap.get(db.DbController.noteBody);
                String color = hashMap.get(db.DbController.noteColor);
                String time = hashMap.get(DbController.noteDate);
                //final int fontSize = Integer.parseInt(hashMap.get(DbController.noteFontSize));

                View view = getLayoutInflater().inflate(R.layout.existing_notes, null);
                view.setOnClickListener(myClickLit);
                view.setTag(id + "");

                TextView tvBody = (TextView) view.findViewById(R.id.tvBody);
                TextView tvDate = (TextView) view.findViewById(R.id.tvDate);

                tvBody.setText(text);
                view.setBackgroundColor(Color.parseColor(color));
                tvDate.setText(time);


                linearLayout.addView(view);

            }
        }

    }


    private void initLayoutDeletion() {

        llDeletion = (LinearLayout) findViewById(R.id.llDelete);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnCancel = (Button) findViewById(R.id.btnCancel);
        //////////////////////////////////////////
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!idsToDelete.isEmpty()) {
                    //delete the selected sticky notes
                    for (int i = 0; i < idsToDelete.size(); i++) {
                        DbController.exeQuery("delete from " + db.DbController.tblNotes + " where " + db.DbController.noteId + "  = '" + idsToDelete.get(i).toString() + "' ");
                    }
                } else {

                }
                llDeletion.setVisibility(View.GONE);
                addStickyNoteToMainView();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llDeletion.setVisibility(View.GONE);
                addStickyNoteToMainView();//for refreh the main view that contains the sticky and removing the checkboxs
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //for the main layout
        if (id == 0) {

            //createShortToast("0");

//            menu.add(0, 0, 0, "New note").setIcon(R.drawable.add_new_memo);
//            menu.add(0, 1, 0, "Delete").setIcon(R.drawable.del_fill);
        }

        //for the alert dialog view
        if (id == 1) {

            //createShortToast("1");
//            menu.add(1, 0, 0 , "New note");
//            menu.add(1, 1, 0, "Font size");
//            menu.add(1, 2, 0, "Remind me");
//            menu.add(1, 3, 0, "Share");

        }

        return true;
    }


    boolean bo2 = true, bo = true;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //add new memo
                //createToast("Add new MeMo");
                //addNewMemoItem();

                //id = 1;//toggle the id  from 0 to 1 to attach the menu to the alert dialog

                if (bo = false && bo2) {

                    String s = etWritingNotes.getText().toString();
                    if (s.trim().length() != 0) {
                        noteBody = s;
                        saveAndAddSticky();
                    } else {

                    }
                    //add this stick note to the main view
                    addStickyNoteToMainView();//refresh the database then recreate the views
                    id = 0;
                    bo = true;

//                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);//to be clickable
//                    getSupportActionBar().setHomeAsUpIndicator(getResources().getDrawable(R.drawable.add_new_memo));
//                    setContentView(R.layout.activity_main);

                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);

                } else {
                    if (bo2 == false) {
                        //editting the seleted sticky note
                        sql = "update " + db.DbController.tblNotes + " set " + DBController.noteBody + " = '" + etWritingNotes.getText().toString() + "' where " +
                                db.DbController.noteId + " =   '" + idForDb + "' ";
                        DbController.exeQuery(sql);
                        createShortToast("Updated");

                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);

                        bo2 = true;
                    } else {
                        addNewMemoItem2();
                        id = 1;
                        createShortToast("hello");
                    }
                }


                break;
            case 0:
                addNewMemoItem2();
                break;
            case 1:
                deleteStickyNotes();

                break;


//            default:
//                return false;
        }
        //return super.onOptionsItemSelected(item);
        return true;
    }


    private void addNewMemoItem2() {


        View view = getSupportActionBar().getCustomView();
        ivAddNewNote = (ImageView) view.findViewById(R.id.ivAddOrBack);
        ivAddNewNote.setImageResource(R.mipmap.ic_action_back);
        ivAddNewNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etWritingNotes.getText().toString().trim().length() == 0){
                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);
                }else{
                    noteBody = etWritingNotes.getText().toString();
                    saveAndAddSticky();
                    //add this stick note to the main view
                    addStickyNoteToMainView();//refresh the da
                    id = 0;
                }
            }
        });

        setContentView(R.layout.add_new_xml);

        ImageView ivColoringEditText = (ImageView) findViewById(R.id.ivColorEditText);
        ivColoringEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createListOfColors(strings);
            }
        });
        etWritingNotes = (EditText) findViewById(R.id.etWritingNotes);
        etWritingNotes.requestFocus();//for showing the soft  input
        

    }

    boolean toggle2 = true;

    private void updateSelectedNote() {

        if (bo2) {
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setCustomView(R.layout.custom_action_bar);
            final View view = getSupportActionBar().getCustomView();


            final ImageView ivAddNewNote = (ImageView) view.findViewById(R.id.ivAddOrBack);
            TextView tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            view1 = (LinearLayout) view.findViewById(R.id.llActionBar);
            ivSearch = (ImageView) view.findViewById(R.id.ivSearchFor);
            ivSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (toggle) {
                        view1.removeAllViews();
                        EditText editText = new EditText(context);
                        editText.setHint("Search for ..");
                        editText.setTypeface(Typeface.DEFAULT_BOLD);
                        editText.addTextChangedListener(textWatcher);
                        editText.setPadding(3, 3, 3, 3);
                        editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        editText.setTextColor(Color.parseColor("#FFFFFF"));
                        editText.setHintTextColor(Color.parseColor("#FFFFFF"));
                        view1.addView(editText);
                        toggle = false;
                    } else {
                        view1.removeAllViews();
                        TextView textView = new TextView(context);
                        textView.setText(" Sticky Notes ");
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                        textView.setTypeface(Typeface.DEFAULT_BOLD);
                        textView.setTextColor(Color.parseColor("#FFFFFF"));
                        textView.setPadding(3, 3, 3, 3);
                        view1.addView(textView);
                        toggle = true;
                    }

                }
            });
            ivAddNewNote.setImageResource(R.mipmap.ic_action_back);//by default
            ivAddNewNote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //editting the seleted sticky note
                    sql = "update " + db.DbController.tblNotes +
                            " set " +
                            DBController.noteBody + " = '" + etWritingNotes.getText().toString() + "', " +
                            DBController.noteColor + " = '" + noteColor + "' " +
                            "where " +
                            db.DbController.noteId + " =   '" + idForDb + "' ";
                    DbController.exeQuery(sql);
                    createShortToast("Updated");


                    Intent intent = getIntent();
                    finish();
                    startActivity(intent);


                }
            });


            setContentView(R.layout.add_new_xml);

            ImageView ivColoringEditText = (ImageView) findViewById(R.id.ivColorEditText);
            ivColoringEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createListOfColors(strings);
                }
            });
            etWritingNotes = (EditText) findViewById(R.id.etWritingNotes);
            etWritingNotes.requestFocus();//for showing the soft  input


            hashMaps = DbController.getData("select * from " + db.DbController.tblNotes + " where " + db.DbController.noteId + " = '" + idForDb + "' ");
            String noteText = hashMaps.get(0).get(db.DbController.noteBody);
            String noteClr = hashMaps.get(0).get(db.DbController.noteColor);
            String noteFont = hashMaps.get(0).get(db.DbController.noteFontSize);
            String noteTime = hashMaps.get(0).get(db.DbController.noteDate);

            etWritingNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(noteFont));
            etWritingNotes.setText(noteText);
            etWritingNotes.setBackgroundColor(Color.parseColor(noteClr));


            bo2 = false;


        }


    }


    private void deleteStickyNotes() {
        linearLayout.removeAllViews();
        /////////////////////////////////////
        //then rebuild all notes with check box to be selected
        hashMaps = DbController.getData("select * from " + db.DbController.tblNotes);//get all data from the db
        //to be more secure
        if (!hashMaps.isEmpty()) {
            for (int i = 0; i < hashMaps.size(); i++) {
                //i need only the text and color and time and ids not fontSize
                String txt = hashMaps.get(i).get(db.DbController.noteBody);
                String time = hashMaps.get(i).get(db.DbController.noteDate);
                String clr = hashMaps.get(i).get(db.DbController.noteColor);
                String id = hashMaps.get(i).get(db.DbController.noteId);//to handle the selected checkboxs with the db


                //get the modified layout that has the checkbox
                LinearLayout linearLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.existing_notes_to_del, null);
                //get the views inside to access them
                TextView tvText = (TextView) linearLayout.findViewById(R.id.tvBody);
                TextView tvTime = (TextView) linearLayout.findViewById(R.id.tvDate);
                //accessing the views
                tvText.setText(txt);
                tvTime.setText(time);
                //////////////////change the color of main view
                linearLayout.setBackgroundColor(Color.parseColor(clr));
                //////get the check box
                CheckBox cbDelete = (CheckBox) linearLayout.findViewById(R.id.cbDelete);
                cbDelete.setTag(String.valueOf(id));//to get it if checked and delete from the table
                cbDelete.setOnCheckedChangeListener(checkedChangeListener);
                ///////////////////
                this.linearLayout.addView(linearLayout);


            }
        }

        llDeletion.setVisibility(View.VISIBLE);

    }

    ArrayList<String> idsToDelete = new ArrayList<String>();

    CheckBox.OnCheckedChangeListener checkedChangeListener = new CheckBox.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                //add the selected id
                idsToDelete.add(buttonView.getTag().toString());
            } else {
                //delete the id from arraylist as the cb is not selected
                if (!idsToDelete.isEmpty()) {
                    for (int i = 0; i < idsToDelete.size(); i++) {
                        if (idsToDelete.get(i) == buttonView.getTag().toString()) {
                            idsToDelete.remove(i);//remove at this pos
                        }
                    }
                }
            }
        }

    };


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            if (id == 0) {
//                createShortToast("menu_alert_dialog");
//                showPopup(this.relativeLayout, R.menu.menu_alert_dialog);
//                return true;
                showOptions(R.layout.options1);
            }
            if (id == 1) {
//                createShortToast("menu_main_layout");
//                showPopup(this.relativeLayout, R.menu.menu_main_layout);
//                return true;
                showOptions(R.layout.options2);
            }
        }
//        return super.onKeyDown(keyCode, event);
        return true;

    }

    AlertDialog adForOptionsMenu = null;
    AlertDialog.Builder builderOptions = null;

    private void showOptions(int id) {
        try {
            View view = getLayoutInflater().inflate(id, null);
            builderOptions = new AlertDialog.Builder(context);
            adForOptionsMenu = builderOptions.create();


            switch (id) {
                case R.layout.options1:
                    view.findViewById(R.id.opt1NewNote).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNewMemoItem2();
                            adForOptionsMenu.dismiss();
                        }
                    });
                    view.findViewById(R.id.opt1Del).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            deleteStickyNotes();
                            adForOptionsMenu.dismiss();
                        }
                    });

                    break;
                case R.layout.options2:
                    view.findViewById(R.id.opt2NewNote).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            addNewMemoItem2();
                            adForOptionsMenu.dismiss();
                        }
                    });
                    view.findViewById(R.id.opt2FontSzie).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adForOptionsMenu.dismiss();
                            changeFontSize();


                        }
                    });
                    view.findViewById(R.id.opt2Remind).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adForOptionsMenu.dismiss();
                        }
                    });
                    view.findViewById(R.id.opt2Share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            adForOptionsMenu.dismiss();
                            shareText();
                        }
                    });

                    break;
            }

            adForOptionsMenu.setView(view);
            adForOptionsMenu.setCancelable(true);
            adForOptionsMenu.setCanceledOnTouchOutside(true);
            adForOptionsMenu.show();

        } catch (Exception e) {
            createAlert(e.getMessage().toString());
        }
    }

    private void shareText() {

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        //intent.putExtra(Intent.EXTRA_SUBJECT, );
        intent.putExtra(Intent.EXTRA_TEXT, this.noteBody);
        startActivity(Intent.createChooser(intent, null));


    }


    int max = 100, min = 20;

    private void changeFontSize() {

//        try {

//            SeekBar seekBar = (SeekBar) getLayoutInflater().inflate(R.layout.font_size, null).findViewById(R.id.seekBar);
//            seekBar.setOnSeekBarChangeListener(this);


        SeekBar seekBar = new SeekBar(context);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setMax(max);
        seekBar.setProgress(seekBar.getMax() / 2);
        //seekBar.setRotation(90f);
        //seekBar.setMinimumHeight(getWindowManager().getDefaultDisplay().getHeight() / 2);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(seekBar);

        AlertDialog alertDialog = builder.create();

        //Grab the window of the dialog, and change the width
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = alertDialog.getWindow();
        lp.copyFrom(window.getAttributes());
        //This makes the dialog take up the full width
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        if (!alertDialog.isShowing())
            alertDialog.show();


    }

    private void addNewMemoItem() {
        //get The customized view
        view = getLayoutInflater().inflate(R.layout.add_new_xml, null);
        View view1 = view.findViewById(R.id.ll2);
        etWritingNotes = (EditText) view.findViewById(R.id.etWritingNotes);//for entering text from the user
        ivColorEditText = (ImageView) view1.findViewById(R.id.ivColorEditText);//for coloring the edittext backgroung
//        etWritingNotes.setOnClickListener(this);
        ivColorEditText.setOnClickListener(this);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                //createShortToast("From Dismiss");
                String s = etWritingNotes.getText().toString();
                if (s.trim().length() != 0) {
                    noteBody = s;
                    saveAndAddSticky();
                    //add this stick note to the main view
                    addStickyNoteToMainView();//refresh the database then recreate the views
                }
                id = 0;//toggle the id from 1 to 0 to attach the menu to the main view of the app that contains all the sticky notes

            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //createToast("From Cancel");
                id = 0;//toggle the id from 1 to 0 to attach the menu to the main view of the app that contains all the sticky notes
            }
        });
        AlertDialog alertDialog = builder.create();
//        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//        lp.copyFrom(alertDialog.getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.FILL_PARENT;
//        lp.height = WindowManager.LayoutParams.FILL_PARENT;
//        alertDialog.show();
//        alertDialog.getWindow().setAttributes(lp);


        if (alertDialog.isShowing()) {
            return;
        }
        //else
        alertDialog.show();


    }

    private void addStickyNoteToMainView() {
        linearLayout.removeAllViews();
        /////////////////////////
        try {
            sql = "select * from " + db.DbController.tblNotes;
            //createShortToast(sql);
            hashMaps = DbController.getData(sql);
            // createAlert(hashMaps.toString());
            if (!hashMaps.isEmpty()) {
                for (int i = 0; i < hashMaps.size(); i++) {
                    HashMap<String, String> hashMap = hashMaps.get(i);
                    String id = hashMap.get(DbController.noteId);
                    String text = hashMap.get(db.DbController.noteBody);
                    String color = hashMap.get(db.DbController.noteColor);
                    String time = hashMap.get(DbController.noteDate);
                    //final int fontSize = Integer.parseInt(hashMap.get(DbController.noteFontSize));

                    View view = getLayoutInflater().inflate(R.layout.existing_notes, null);
                    View view2 = view.findViewById(R.id.llBla1);
                    view2.setOnClickListener(myClickLit);
                    view2.setTag(id + "");

                    TextView tvBody = (TextView) view2.findViewById(R.id.tvBody);
                    TextView tvDate = (TextView) view2.findViewById(R.id.tvDate);

                    tvBody.setText(text);
                    view2.setBackgroundColor(Color.parseColor(color));
                    tvDate.setText(time);


                    linearLayout.addView(view);

                }
            }
        } catch (Exception e) {
            createAlert(e.getMessage().toString());
        }
    }


    View.OnClickListener myClickLit = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                ///Toggle the id of menu
                id = 1;
                //get the id from the selevted view tag to get the content from the database
                idForDb = v.getTag().toString();
                updateSelectedNote();

//                hashMaps = DbController.getData("select * from " + db.DbController.tblNotes + " where " + db.DbController.noteId + " = '" + idForDb + "' ");
//                String noteText = hashMaps.get(0).get(db.DbController.noteBody);
//                String noteClr = hashMaps.get(0).get(db.DbController.noteColor);
//                String noteFont = hashMaps.get(0).get(db.DbController.noteFontSize);
//                String noteTime = hashMaps.get(0).get(db.DbController.noteDate);
//
//
//                /////////////////////
//                View view1 = getLayoutInflater().inflate(R.layout.add_new_xml, null);
//                View view2 = view1.findViewById(R.id.ll2);
//                final EditText etWritingNotes = (EditText) view1.findViewById(R.id.etWritingNotes);//for entering text from the user
//                etWritingNotes.setTextSize(TypedValue.COMPLEX_UNIT_SP, Float.parseFloat(noteFont));
//                etWritingNotes.setText(noteText);
//                etWritingNotes.setBackgroundColor(Color.parseColor(noteClr));
//                ImageView ivColorEditText = (ImageView) view2.findViewById(R.id.ivColorEditText);//for coloring the edittext background
//                ivColorEditText.setOnClickListener(this);
//
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setView(view1);
//                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
//                    @Override
//                    public void onDismiss(DialogInterface dialog) {
//                        //editting the seleted sticky note
//                        sql = "update " + db.DbController.tblNotes + " set " + DBController.noteBody + " = '" + etWritingNotes.getText().toString() + "' where " +
//                                db.DbController.noteId + " =   '" + idForDb + "' ";
//                        DbController.exeQuery(sql);
//                        //createShortToast("Updated");
//                        ////////refresh the main View
//
//                        id = 0;//toggle the id from 1 to 0 to attach the menu to the main view of the app that contains all the sticky notes
//                    }
//                });
//                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
//                    @Override
//                    public void onCancel(DialogInterface dialog) {
//                        //createShortToast("From Cancel");
//                        id = 0;//toggle the id from 1 to 0 to attach the menu to the main view of the app that contains all the sticky notes
//                    }
//                });
//                AlertDialog alertDialog = builder.create();
//                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//                lp.copyFrom(alertDialog.getWindow().getAttributes());
//                lp.width = WindowManager.LayoutParams.FILL_PARENT;
//                lp.height = WindowManager.LayoutParams.FILL_PARENT;
//                alertDialog.show();
//                alertDialog.getWindow().setAttributes(lp);
//
//
//                if (alertDialog.isShowing()) {
//                    return;
//                }
//                //else
//                alertDialog.show();
            } catch (Exception e) {
                createAlert(e.getMessage().toString());
            }
        }
    };

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Calendar calendar = Calendar.getInstance();

    private void saveAndAddSticky() {

        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        try {
            //insert data in my database
            sql = "insert into " + DbController.tblNotes + "(" +
                    DbController.noteBody + "," +
                    DbController.noteColor + ", " +
                    DbController.noteDate + ", " +//the time
                    DbController.noteFontSize +
                    ") values ( '" + noteBody + "', '" + noteColor + "', '" +
                    sdf.format(date) + "' , " + "'" +
                    String.valueOf(etWritingNotes.getTextSize()) + "' " +
                    ")";
            DbController.exeQuery(sql);
            //createShortToast("Inserted");
            //createAlert(sql);
            //DbController.insertDataInDb(s, color, Calendar.getInstance().getTime().toString(), etWritingNotes.getTextSize() + "");

        } catch (Exception e) {
            //createAlert(e.getMessage().toString());
        }

    }


    public void createAlert(String msg) {
        new AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();

    }


    private void createLongToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    private void createShortToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {
        if (v.equals(this.ivColorEditText)) {
            //Show the the list of colors
            createListOfColors(getResources().getStringArray(R.array.color_list));
        }

    }


    AlertDialog.Builder builder = null;
    AlertDialog alertDialog = null;

    private void createListOfColors(/*the array of colors */final String[] strings) {

        /////////////////////////////

        LinearLayout.LayoutParams layoutParams = new
                LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(5, 5, 5, 5);
        ////////////////////////////
        View view = getLayoutInflater().inflate(R.layout.color_list, null);
        View view1 = view.findViewById(R.id.svColorList);
        LinearLayout linearLayout = (LinearLayout) view1.findViewById(R.id.llColorList);

        for (int i = 0; i < strings.length; i++) {
            final int f = i;
            TextView textView = new TextView(context);
//            textView.setBackgroundColor(Color.parseColor(strings[i]));///get the string color then parse it into color
            textView.setBackgroundColor(Color.parseColor(strings[f]));
            final AlertDialog finalAlertDialog = alertDialog;
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    etWritingNotes.setBackgroundColor(Color.parseColor(strings[f]));
                    etWritingNotes.setBackgroundColor(Color.parseColor(strings[f]));
                    noteColor = strings[f] + "";
                    ////////////////////
                    alertDialog.dismiss();
                }
            });
            textView.setLayoutParams(layoutParams);
            //textView.setBackgroundResource(R.drawable.rounded);
            textView.setPadding(20, 20, 20, 20);
            ///////////////////////////////////////////
            linearLayout.addView(textView);

        }

        builder = new AlertDialog.Builder(context);
        builder.setView(view);
        alertDialog = builder.create();
        if (alertDialog.isShowing())
            return;
        alertDialog.show();


    }

    @Override
    public void onBackPressed() {
        if (true) {
            finish();
        } else {
            super.onBackPressed();
        }


    }

    public void showPopup(View v, int menuId) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(menuId, popup.getMenu());
        popup.show();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (progress <= max && progress >= min) {
            this.etWritingNotes.setTextSize(TypedValue.COMPLEX_UNIT_PX, progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }


}
