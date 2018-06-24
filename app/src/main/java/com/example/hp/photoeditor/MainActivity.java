package com.example.hp.photoeditor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rockerhieu.emojicon.EmojiconEditText;
import com.rockerhieu.emojicon.EmojiconGridFragment;
import com.rockerhieu.emojicon.EmojiconTextView;
import com.rockerhieu.emojicon.EmojiconsFragment;
import com.rockerhieu.emojicon.emoji.Emojicon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener ,EmojiconGridFragment.OnEmojiconClickedListener,
        EmojiconsFragment.OnEmojiconBackspaceClickedListener {

    private static final int My_PERMISSION_REQUEST = 1;

    ImageView imageView, textImage;

    Integer REQUEST_CAMERA = 1, SELECT_FILE = 0;

    public static Bitmap bitmap;

    Button Edit, Write, Draw, Emojis, Save, Done, Done2, Done3, Clear;

    FloatingActionButton fab;

    EditText inputText;
    TextView writtentext;

    EmojiconEditText EditEmojicon;
    EmojiconTextView TxtEmojicon;

    String currentImage = "";

    Bitmap alteredBitmap;
    Canvas canvas;
    Paint paint;
    Matrix matrix;
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //ask for permission
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, My_PERMISSION_REQUEST);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, My_PERMISSION_REQUEST);
            }
        } else {

        }

        imageView = (ImageView) findViewById(R.id.imageview);


        Edit = (Button) findViewById(R.id.edit);
        Write = (Button) findViewById(R.id.write);
        Draw = (Button) findViewById(R.id.draw);
        Emojis = (Button) findViewById(R.id.emojis);
        Save = (Button) findViewById(R.id.save);
        Done = (Button) findViewById(R.id.done);
        Done2 = (Button) findViewById(R.id.done2);
        Done3 = (Button) findViewById(R.id.done3);
        Clear = (Button) findViewById(R.id.clear);

        inputText = (EditText) findViewById(R.id.imagetext);
        writtentext = (TextView) findViewById(R.id.textview);

        EditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);
        TxtEmojicon = (EmojiconTextView) findViewById(R.id.txtEmojicon);

        Edit.setVisibility(View.GONE);
        Write.setVisibility(View.GONE);
        Draw.setVisibility(View.GONE);
        Emojis.setVisibility(View.GONE);
        Save.setVisibility(View.GONE);
        inputText.setVisibility(View.GONE);
        writtentext.setVisibility(View.GONE);
        Done.setVisibility(View.GONE);
        EditEmojicon.setVisibility(View.GONE);
        TxtEmojicon.setVisibility(View.GONE);
        Done2.setVisibility(View.GONE);
        Done3.setVisibility(View.GONE);
        Clear.setVisibility(View.GONE);

        Edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Edit.setVisibility(View.GONE);
                Write.setVisibility(View.VISIBLE);
                Draw.setVisibility(View.VISIBLE);
                Emojis.setVisibility(View.VISIBLE);
                Save.setVisibility(View.VISIBLE);

            }
        });

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Click on any option", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SelectImage();
            }
        });

        Write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputText.setVisibility(View.VISIBLE);
                writtentext.setVisibility(View.VISIBLE);
                Done.setVisibility(View.VISIBLE);

                inputText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        writtentext.setText(inputText.getText() + "");
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

            }
        });
        Done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Done.setVisibility(View.GONE);
                inputText.setVisibility(View.GONE);

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);


                writtentext.buildDrawingCache();
                textImage = (ImageView) findViewById(R.id.textviewImage);
                textImage.setImageBitmap(writtentext.getDrawingCache());
            }
        });

        Emojis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditEmojicon.setVisibility(View.VISIBLE);
                TxtEmojicon.setVisibility(View.VISIBLE);
                Done2.setVisibility(View.VISIBLE);

                EditEmojicon.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        TxtEmojicon.setText(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }

                });
                setEmojiconFragment();
            }

        });

        Done2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditEmojicon.setVisibility(View.GONE);
                removeEmojiconFragment();
                Toast.makeText(getApplicationContext(), "Drag Emojis Anywhere", Toast.LENGTH_SHORT).show();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View content = findViewById(R.id.lay);
                Bitmap bitmap = getScreenShot(content);
                currentImage = "image" + System.currentTimeMillis() + ".png";
                store(bitmap, currentImage);
            }
        });

        TxtEmojicon.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    v.setX(event.getRawX() - v.getWidth() / 2.0f);
                    v.setY(event.getRawY() - v.getHeight() / 2.0f);
                }

                return true;
            }

        });

        Draw.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void onClick(View view) {
                Done3.setVisibility(View.VISIBLE);
                Clear.setVisibility(View.VISIBLE);


                alteredBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                        .getHeight(), bitmap.getConfig());
                canvas = new Canvas(alteredBitmap);
                paint = new Paint();
                paint.setColor(Color.GREEN);
                paint.setStrokeWidth(5);
                matrix = new Matrix();
                canvas.drawBitmap(bitmap, matrix, paint);

                imageView.setImageBitmap(alteredBitmap);

            }

        });
        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canvas.drawBitmap(bitmap, matrix, paint);
            }
        });
        Done3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Done3.setVisibility(View.GONE);
                Clear.setVisibility(View.GONE);
            }
        });
        imageView.setOnTouchListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case My_PERMISSION_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(MainActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {
                    Toast.makeText(this, "No permission granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    private void SelectImage() {

        final CharSequence[] items = {"Camera", "Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Add Photo");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (items[i].equals("Camera")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(intent, REQUEST_CAMERA);
                    }
                } else if (items[i].equals("Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[i].equals("Cancel")) {
                    dialogInterface.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                bitmap = (Bitmap) data.getExtras().get("data");
                imageView.setImageBitmap(bitmap);
                Visibility();


            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImageUri));
                    imageView.setImageBitmap(bitmap);
                    Visibility();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void Visibility() {
        Edit.setVisibility(View.VISIBLE);
        fab.setVisibility(View.GONE);

    }

    public static Bitmap getScreenShot(View view) {

        view.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
        view.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public void store(Bitmap bm, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/EditedImage";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dirPath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setEmojiconFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EmojiconsFragment myFragment = new EmojiconsFragment();
        fragmentTransaction.add(R.id.emojicon, myFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    private void removeEmojiconFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @Override
    public void onEmojiconClicked(Emojicon emojicon) {
        EmojiconsFragment.input(EditEmojicon, emojicon);

    }

    @Override
    public void onEmojiconBackspaceClicked(View v) {
        EmojiconsFragment.backspace(EditEmojicon);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                downx = event.getX();
                downy = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                downx = upx;
                downy = upy;
                break;
            case MotionEvent.ACTION_UP:
                upx = event.getX();
                upy = event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                imageView.invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }
}

