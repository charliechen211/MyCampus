package com.stpi.campus.activity.otherService;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import com.stpi.campus.WebContentActivity;
import com.stpi.campus.entity.Node;
import com.stpi.campus.entity.PaintNode;
import com.stpi.campus.items.user.UserInfo;
import com.stpi.campus.parser.ContentParser;
import com.stpi.campus.util.Constants;
import com.stpi.campus.util.Net;
import com.stpi.campus.util.SystemUiHider;
import com.stpi.campus.activity.merchant.MerchantDetailActivity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see com.stpi.campus.util.SystemUiHider
 */
public class SearchActivity extends PApplet {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;
    /**
     * The flags to pass to {@link com.stpi.campus.util.SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private final PVector ref = new PVector();
    private final PVector trans = new PVector();
    private final PVector lastMove = new PVector();
    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            SearchActivity.this.mSystemUiHider.hide();
        }
    };
    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;
    private RelativeLayout surfaceLayout;
    private RelativeLayout processBar;
    private ImageView imageView;
    private List<PaintNode> nodes;
    private ContentParser contentParser;
    private String resultState = "error";
    private String error;
    private boolean loaded = false;
    private int adIndex = 1;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    if (adIndex == 1) {
                        adIndex = 2;
                        imageView.setImageResource(com.stpi.campus.R.drawable.ad2);
                    } else {
                        adIndex = 1;
                        imageView.setImageResource(com.stpi.campus.R.drawable.ad);
                    }
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private Timer adTimer;
    private PImage image;
    private PImage recommendImage;
    private int maxRadius = 60;
    private Boolean loadSearch = false;
    private String keyword;
    private String type = "one";
    private String recomment = "0";
    // search word
    private SearchView.OnQueryTextListener searchClickListener = new SearchView.OnQueryTextListener() {

        @Override
        public boolean onQueryTextSubmit(String query) {
            Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("loadSearch", true);
            bundle.putString("keyword", searchView.getQuery().toString());
            bundle.putString("type", "one");
            bundle.putString("recommended", recomment);
            bundle.putString("id", "");
            intent.putExtras(bundle);
            startActivity(intent);
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            return false;
        }
    };
    private String id = "";
    private SearchView searchView;
    private Button.OnClickListener recommentClickListener = new Button.OnClickListener() {

        public void onClick(View v) {
            Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("loadSearch", true);
            bundle.putString("keyword", searchView.getQuery().toString());
            bundle.putString("type", "one");
            bundle.putString("recommented", "1");
            bundle.putString("id", "");
            intent.putExtras(bundle);
            startActivity(intent);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.stpi.campus.R.layout.activity_search);

        Bundle bundle = this.getIntent().getExtras();
        loadSearch = bundle.getBoolean("loadSearch");
        type = bundle.getString("type");
        keyword = bundle.getString("keyword");
        recomment = bundle.getString("recommented");
        id = bundle.getString("id");
        init();
    }

    private void init() {
        surfaceLayout = (RelativeLayout) findViewById(com.stpi.campus.R.id.layout_surface);
        processBar = (RelativeLayout) findViewById(com.stpi.campus.R.id.process_layout);
        surfaceLayout.addView(surfaceView, sketchWidth(), sketchHeight());
        imageView = (ImageView) findViewById(com.stpi.campus.R.id.img_ad);

        //searchEditText.setText(keyword);

        //showButton.setOnClickListener(searchClickListener);
        //recommentButton.setOnClickListener(recommentClickListener);

        contentParser = new ContentParser();
        nodes = new ArrayList<PaintNode>();

        adTimer = new Timer();
        adTimer.scheduleAtFixedRate(new SwitchTimerTask(), 0, 2000);

        //final View controlsView = findViewById(com.stpi.campus.R.id.fullscreen_content_controls);
        //final View contentView = findViewById(com.stpi.campus.R.id.fullscreen_content);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        /*this.mSystemUiHider = SystemUiHider.getInstance(this, surfaceLayout,
                HIDER_FLAGS);
        this.mSystemUiHider.setup();
        this.mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (this.mControlsHeight == 0) {
                                this.mControlsHeight = controlsView.getHeight();
                            }
                            if (this.mShortAnimTime == 0) {
                                this.mShortAnimTime = getResources()
                                        .getInteger(
                                                android.R.integer.config_shortAnimTime);
                            }
                            controlsView
                                    .animate()
                                    .translationY(
                                            visible ? 0 : this.mControlsHeight)
                                    .setDuration(this.mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE
                                    : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });*/
        // Set up the user interaction to manually show or hide the system UI.
        /*surfaceLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    SearchActivity.this.mSystemUiHider.toggle();
                } else {
                    SearchActivity.this.mSystemUiHider.show();
                }
            }
        });*/

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        searchView = (SearchView) findViewById(com.stpi.campus.R.id.searchView2);
        searchView.setOnTouchListener(this.mDelayHideTouchListener);
        if (keyword != null) {
            searchView.setQuery(keyword, true);
        }

        searchView.setOnQueryTextListener(searchClickListener);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (loadSearch == true && !loaded) {
            SearchLoad searchLoad = new SearchLoad();
            searchLoad.execute(keyword, type, id, recomment);
            loaded = true;
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        // delayedHide(100);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        this.mHideHandler.removeCallbacks(this.mHideRunnable);
        this.mHideHandler.postDelayed(this.mHideRunnable, delayMillis);
    }

    @Override
    public void setup() {
        super.setup();
        maxRadius = width / 6;
        background(Color.WHITE);
        smooth();
        //noLoop();
        ellipseMode(RADIUS);
        imageMode(CENTER);
        textAlign(CENTER, CENTER);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), com.stpi.campus.R.drawable.ic_expand);
        image = new PImage(bitmap);
        image.resize((int) (maxRadius * 0.6), (int) (maxRadius * 0.6));
        image.loadPixels();
        image.parent = this;

        // recommend image
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), com.stpi.campus.R.drawable.ic_recommend);
        recommendImage = new PImage(bitmap2);
        recommendImage.resize((int) (maxRadius * 0.6), (int) (maxRadius * 0.6));
        recommendImage.loadPixels();
        recommendImage.parent = this;
    }

    @Override
    public void draw() {
        background(Color.WHITE);
        drawContents();
    }

    private void drawContents() {
        //  draw edge first
        for (int i = 1; i < nodes.size(); i++) {
            drawEdge(nodes.get(0), nodes.get(i));
        }


        for (int i = 0; i < nodes.size(); i++) {
            // draw nodes
            fill(nodes.get(i).getColor());
            noStroke();
            ellipse(nodes.get(i).getPosX(),
                    nodes.get(i).getPosY(),
                    nodes.get(i).getRadius(), nodes.get(i).getRadius());
            //Log.i("node", i+ "X: " + nodes.get(i).getPosX() + " Y: " + nodes.get(i).getPosY());

            // draw title
            fill(Color.WHITE);
            textSize(nodes.get(i).getRadius() / 60 * 20);
            text(nodes.get(i).getNode().getName(),
                    nodes.get(i).getPosX() - nodes.get(i).getRadius() / sqrt(2) + 2,
                    nodes.get(i).getPosY() - nodes.get(i).getRadius() / sqrt(2) + 2,
                    sqrt(2) * nodes.get(i).getRadius() - 4,
                    sqrt(2) * nodes.get(i).getRadius() - 4);

            // draw expand
            image(image, nodes.get(i).getPosX() - nodes.get(i).getRadius() / sqrt(2)
                    , nodes.get(i).getPosY() - nodes.get(i).getRadius() / sqrt(2));
            if (nodes.get(i).getNode().getRecommeded() == 1) {
                // draw recommend
                image(recommendImage, nodes.get(i).getPosX() + nodes.get(i).getRadius() / sqrt(2)
                        , nodes.get(i).getPosY() - nodes.get(i).getRadius() / sqrt(2));
            }
        }
    }

    private void drawEdge(PaintNode sourceNode, PaintNode targetNode) {
//		Float x1 = Float.valueOf(width/2);
//		Float y1 = Float.valueOf(height/2);
//		Float x2 = 200f;
//		Float y2 = 200f;

        Float x1 = sourceNode.getPosX();
        Float y1 = sourceNode.getPosY();
        Float x2 = targetNode.getPosX();
        Float y2 = targetNode.getPosY();


        //Log.i("edge begin", "X: " + x1 + " Y: " + y1);
        //Log.i("edge end", "X: " + x2 + " Y: " + y2);

        // Curved edgs
        PVector direction = new PVector(x2, y2);
        direction.sub(new PVector(x1, y1));
        float length = direction.mag();
        direction.normalize();

        float factor = 0.2f * length;

        // normal vector to the edge
        PVector n = new PVector(direction.y, -direction.x);
        n.mult(factor);

        // first control point
        PVector v1 = new PVector(direction.x, direction.y);
        v1.mult(factor);
        v1.add(new PVector(x1, y1));
        //v1.add(n)
        if (targetNode.isRightDirection()) {
            v1.add(n);
        } else {
            v1.sub(n);
        }
        // second control point
        PVector v2 = new PVector(direction.x, direction.y);
        v2.mult(-factor);
        v2.add(new PVector(x2, y2));
        //v2.add(n);
        if (targetNode.isRightDirection()) {
            v2.add(n);
        } else {
            v2.sub(n);
        }

        stroke((sourceNode.getColor() + targetNode.getColor()) / 2);
        strokeWeight(targetNode.getWeight());
        noFill();
        bezier(x1, y1, v1.x, v1.y, v2.x, v2.y, x2, y2);
    }

    @Override
    public void mouseClicked() {
        Log.i("Clicked", "X: " + mouseX + " Y: " + mouseY);
        for (int i = nodes.size() - 1; i >= 0; i--) {
            // check if click the extend than check if in the node
            if (isInCircle(mouseX, mouseY, nodes.get(i).getPosX() - nodes.get(i).getRadius() / sqrt(2),
                    nodes.get(i).getPosY() - nodes.get(i).getRadius() / sqrt(2), 20)) {
                Log.i("Click Extend", String.valueOf(i));
                Intent intent = new Intent(SearchActivity.this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("loadSearch", true);
                bundle.putString("keyword", nodes.get(i).getNode().getName());
                bundle.putString("type", "two");
                bundle.putString("recommended", recomment);
                bundle.putString("id", nodes.get(i).getNode().getId());
                intent.putExtras(bundle);
                startActivity(intent);
                break;
            } else if (isInCircle(mouseX, mouseY, nodes.get(i).getPosX(),
                    nodes.get(i).getPosY(), nodes.get(i).getRadius())) {
                Log.i("Click Node", String.valueOf(i));
                //String urlString = "http://"+ Constants.loginServer+"/SearchJson/shop/view/"+
                //        nodes.get(i).getNode().getId() + ".json";
                //openBrowser(urlString);
                Intent intent = new Intent(SearchActivity.this, MerchantDetailActivity.class);
                intent.putExtra("shop_id", nodes.get(i).getNode().getId());
                startActivity(intent);
                break;
            }
        }
    }

    private boolean isInCircle(float x, float y, float centerX, float centerY, float radius) {
        if ((x - centerX) * (x - centerX) + (y - centerY) * (y - centerY) < radius * radius) {
            return true;
        }
        return false;
    }

    @Override
    public void mouseDragged() {
        //Log.i("Dragged", "X: " + mouseX + " Y: " + mouseY);
        trans.set(mouseX, mouseY, 0);
        trans.sub(ref);
        boolean isDragNode = false;
        for (int i = nodes.size() - 1; i >= 0; i--) {
            if (isInCircle(mouseX, mouseY, nodes.get(i).getPosX(),
                    nodes.get(i).getPosY(), nodes.get(i).getRadius())) {
                //	Log.i("Drag Node", String.valueOf(i));
                PVector temp = new PVector(nodes.get(i).getPosX(), nodes.get(i).getPosY());
                temp.add(trans);
                nodes.get(i).setPosX(temp.x);
                nodes.get(i).setPosY(temp.y);
                isDragNode = true;
                break;
            }
        }


        if (!isDragNode) {
            //Log.i("Drag Graph", "Graph");
            for (int i = nodes.size() - 1; i >= 0; i--) {
                PVector temp = new PVector(nodes.get(i).getPosX(), nodes.get(i).getPosY());
                temp.add(trans);
                nodes.get(i).setPosX(temp.x);
                nodes.get(i).setPosY(temp.y);
            }
        }
        ref.set(mouseX, mouseY, 0);
        redraw();
    }

    @Override
    public void mousePressed() {
        //Log.i("Pressed", "X: " + mouseX + " Y: " + mouseY);
        ref.set(mouseX, mouseY, 0);
        redraw();
    }

    @Override
    public void mouseReleased() {
        //Log.i("Released", "X: " + mouseX + " Y: " + mouseY);
        lastMove.set(trans);
        redraw();
    }

    private void openBrowser(String url) {
        Intent intent = new Intent(SearchActivity.this, WebContentActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void searchWithQUery(String keword, String type, String id, String recommented) {
        nodes = new ArrayList<PaintNode>();

        List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
        paramsList.add(new BasicNameValuePair(Constants.NET_PARA_USERID, UserInfo.userId));
        paramsList.add(new BasicNameValuePair(Constants.NET_PARA_TYPE, Constants.type));

        if ("one".equals(type)) {
            paramsList.add(new BasicNameValuePair("recommended", recommented));
            paramsList.add(new BasicNameValuePair(Constants.NET_PARA_QUERY, keword));
        } else {
            paramsList.add(new BasicNameValuePair(Constants.NET_PARA_ID, id));
        }

        String content = "";
        try {
            if ("one".equals(type)) {
                content = new Net().post(
                        this.getString(com.stpi.campus.R.string.first_search_url),
                        //"http://"+Constants.loginServer+"/SearchJson/search/firstSearch.json",
                        paramsList);
            } else {
                content = new Net().post(
                        this.getString(com.stpi.campus.R.string.second_search_url),
                        //"http://"+Constants.loginServer+"/SearchJson/search/secondSearch.json",
                        paramsList);
            }
            initPaintNode(content);
            resultState = "success";
        } catch (Exception e) {
            e.printStackTrace();
            error = "搜索失败，请稍后再试";
        }
    }

    private void initPaintNode(String content) {
        List<Node> valueNodes = contentParser.parse(content);

        if (valueNodes.size() > 0) {
            int showSize = Constants.MAX_NODE_NUMBER_PERPAGE < valueNodes
                    .size() ? Constants.MAX_NODE_NUMBER_PERPAGE : valueNodes
                    .size();
            PaintNode node = new PaintNode(valueNodes.get(0), width / 2, height / 2,
                    com.stpi.campus.R.color.default_center_color, 70, (float) (Math.random() * 2 + 1), true);
            nodes.add(node);

            double max = Math.sqrt(width * width + height * height) * 0.7;


            for (int i = 1; i < showSize; i++) {
                double rand = Math.random() * 360;
                float weight = valueNodes.get(i).getWeight();
                if (weight <= 1 && weight >= 0.5) {
                    node = new PaintNode(valueNodes.get(i),
                            (float) (Math.sin(rand) * (weight * 0.9) * max / 2 + width / 2),
                            (float) (Math.cos(rand) * (weight * 0.9) * max / 2 + height / 2),
                            (int) (Math.random() * Color.BLACK),
                            (float) ((1.5 - weight) * maxRadius), (float) (weight * 6),
                            Math.random() > 0.5f ? true : false
                    );
                    nodes.add(node);
                }
            }
        }
    }

    private class SearchLoad extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            processBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            searchWithQUery(params[0], params[1], params[2], params[3]);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            processBar.setVisibility(View.GONE);
            if ("success".equals(resultState) == false) {
                Toast.makeText(getApplicationContext(), error, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    class SwitchTimerTask extends TimerTask {

        @SuppressLint("HandlerLeak")
        @Override
        public void run() {
            // TODO Auto-generated method stub
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    }
}
