package com.example.supermario.mymovies;

import android.animation.ObjectAnimator;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.supermario.mymovies.extra.HorizontalScrollViewListener;
import com.example.supermario.mymovies.extra.ObservableHorizontalScrollView;

import static com.example.supermario.mymovies.R.id.coversScrollView;
import static com.example.supermario.mymovies.R.id.miniDaysLayout;

public class DisplayFragment extends Fragment implements HorizontalScrollViewListener {


    private ArrayAdapter<String> coversAdapter;
    private boolean preloaded = false;
    private ViewHolder viewHolders[];
    private ObservableHorizontalScrollView observableHorizontalScrollView;
    LinearLayout moviesDisplay;

    public DisplayFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.display_fragment, container, false);

        moviesDisplay = (LinearLayout) rootView.findViewById(miniDaysLayout);
        //  miniDaysLayout.removeAllViews();
        preloaded = false;

        viewHolders = new ViewHolder[4];
        loadMovies(moviesDisplay, container, inflater);
        observableHorizontalScrollView = (ObservableHorizontalScrollView) rootView.findViewById(coversScrollView);
        //  observableHorizontalScrollView.setScrollViewListener(this);
        observableHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == event.ACTION_UP) {
                    if ((observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6)) && (observableHorizontalScrollView.getScrollX() < (moviesDisplay.getWidth() / 6) * 3)) {
                        scrollCovers(moviesDisplay, moviesDisplay.getWidth() / 3);
                    } else if ((observableHorizontalScrollView.getScrollX() < (moviesDisplay.getWidth() / 6))) {
                        if (front) {
                            scrollCovers(moviesDisplay, 0);
                        } else {
                            reloadMovieCoverLayotBackward(moviesDisplay);
                        }
                    } else if (observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6) * 3) {
                        if (end) {
                            scrollCovers(moviesDisplay, ((moviesDisplay.getWidth() / 6) * 5));
                        } else {
                            reloadMovieCoverLayotForward(moviesDisplay);
                        }
                    }
                }
                return false;
            }
        });
        return rootView;
    }


    private void loadMovies(LinearLayout moviesDisplay, ViewGroup container, LayoutInflater inflater) {
        if (!preloaded) {
            loadMovieCoverLayot(moviesDisplay, container, inflater);
        }

    }

    boolean center = false;

    private void scrollCovers(LinearLayout moviesDisplay, int scrollValue) {
        ObjectAnimator.ofInt(observableHorizontalScrollView, "scrollX", scrollValue).setDuration(400).start();
    }

    boolean front = true;
    boolean end = false;

    private void reloadMovieCoverLayotForward(LinearLayout moviesDisplay) {
        ViewHolder temp = viewHolders[0];
        moviesDisplay.removeView(viewHolders[0].movieCoverLayout);
        viewHolders[0] = viewHolders[1];
        viewHolders[1] = viewHolders[2];
        viewHolders[2] = temp;
        moviesDisplay.addView(temp.movieCoverLayout);
        // System.out.println(" moved to front "+temp.buttons[0].getText());


        // observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6)*2 , 0);

        scrollCovers(moviesDisplay, ((moviesDisplay.getWidth() / 6) * 2));
        front = false;
    }


    private void reloadMovieCoverLayotBackward(LinearLayout moviesDisplay) {
        ViewHolder temp = viewHolders[2];
        viewHolders[2] = viewHolders[1];
        viewHolders[1] = viewHolders[0];
        viewHolders[0] = temp;
        moviesDisplay.removeAllViews();
        moviesDisplay.addView(viewHolders[0].movieCoverLayout);
        moviesDisplay.addView(viewHolders[1].movieCoverLayout);
        moviesDisplay.addView(viewHolders[2].movieCoverLayout);


        // observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6)*2 , 0);

        scrollCovers(moviesDisplay, ((moviesDisplay.getWidth() / 6) * 2));

    }

    private void loadMovieCoverLayot(LinearLayout moviesDisplay, ViewGroup container, LayoutInflater inflater) {
        int i = 0;

        int width = this.getResources().getDisplayMetrics().widthPixels;
        int rowCapacity = getRowCapacity();


        ViewHolder viewHolder;


        for (int n = 0; n < 3; n++) {
            viewHolder = new ViewHolder();
            viewHolder.buttons = new Button[rowCapacity * 2];
            LinearLayout movieCoverLayout = (LinearLayout) inflater.inflate(R.layout.movie_cover_table_layout, container, false);
            movieCoverLayout.setId(n);
            LinearLayout.LayoutParams lps = new LinearLayout.LayoutParams(width, LayoutParams.MATCH_PARENT
            );
            movieCoverLayout.setLayoutParams(lps);

            LinearLayout[] rows = new LinearLayout[2];
            rows[0] = (LinearLayout) movieCoverLayout.findViewById(R.id.movie_cover_layout_top);
            rows[1] = (LinearLayout) movieCoverLayout.findViewById(R.id.movie_cover_layout_bottom);
            viewHolder.movieCoverLayout = movieCoverLayout;
            int button_position = 0;
            for (LinearLayout row : rows) {
                Button btn;
                for (int s = 0; s < rowCapacity; s++) {
                    btn = new Button(getActivity());
                    btn.setText("kos " + i);
                    btn.setId(i);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT
                    );
                    lp.weight = 1.0f;


                    //     btn.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.cover_button));

                    btn.setLayoutParams(lp);
                    row.addView(btn);
                    viewHolder.buttons[button_position] = btn;
                    LinearLayout tmp = (LinearLayout) btn.getParent().getParent();

                    final int id = tmp.getId();
                    btn.setOnClickListener(new android.view.View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            // TODO Auto-generated method stub
                            System.out.println("layout nr:  " + id);


                        }
                    });

                    button_position++;
                    i++;
                }
            }
            moviesDisplay.addView(movieCoverLayout);
            viewHolders[n] = viewHolder;
        }
        preloaded = true;
    }


    private int getRowCapacity() {
        if (getResources().getConfiguration().orientation == getResources().getConfiguration().ORIENTATION_LANDSCAPE) {
            return 3;
        }
        return 2;
    }

    private class ViewHolder {
        public LinearLayout movieCoverLayout;
        public Button[] buttons;
    }

    @Override
    public void onScrollChanged(ObservableHorizontalScrollView scroll, int x, int y, int oldx, int oldy) {
        Rect scrollBounds = new Rect();

        observableHorizontalScrollView.getHitRect(scrollBounds);

//        if (x > oldx) {
//            if (viewHolders[0].movieCoverLayout.getLocalVisibleRect(scrollBounds)) {
//            } else {
//                reloadMovieCoverLayotForward(moviesDisplay);
//            }
//        } else {
//            if (viewHolders[2].movieCoverLayout.getLocalVisibleRect(scrollBounds)) {
//            } else {
//                //    reloadMovieCoverLayotBackward(moviesDisplay);
//            }
//        }
        if (observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6)) {
            if (!center) {
                // reloadMovieCoverLayotForward(moviesDisplay);
            }
        }

    }
}