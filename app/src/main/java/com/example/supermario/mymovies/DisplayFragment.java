package com.example.supermario.mymovies;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;

import com.example.supermario.mymovies.extra.HorizontalScrollViewListener;
import com.example.supermario.mymovies.extra.ObservableHorizontalScrollView;
import com.example.supermario.mymovies.util.AsyncResponse;
import com.example.supermario.mymovies.util.IdCoverHolder;
import com.example.supermario.mymovies.util.MoviesCoversDAO;

import static com.example.supermario.mymovies.R.id.coversScrollView;
import static com.example.supermario.mymovies.R.id.miniDaysLayout;

public class DisplayFragment extends Fragment implements HorizontalScrollViewListener, AsyncResponse {


    // private ArrayAdapter<String> coversAdapter;
    private boolean preloaded = false;
    private ViewHolder viewHolders[];
    private ObservableHorizontalScrollView observableHorizontalScrollView;
    private LinearLayout moviesDisplay;
    SparseArray<IdCoverHolder> coverHolders;
    ViewGroup container;

    public DisplayFragment() {
    }

    int pageNumber = 1;//cant be lower than 1
    int presentCover = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MoviesCoversDAO moviesCoversDAO = new MoviesCoversDAO();
        moviesCoversDAO.asyncResponse = this;
        moviesCoversDAO.execute(MoviesCoversDAO.SEARCH_POPULAR, String.valueOf(pageNumber));


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.display_fragment, container, false);
        this.container = container;
        moviesDisplay = (LinearLayout) rootView.findViewById(miniDaysLayout);
        //  miniDaysLayout.removeAllViews();
        preloaded = false;

        viewHolders = new ViewHolder[3];


        observableHorizontalScrollView = (ObservableHorizontalScrollView) rootView.findViewById(coversScrollView);
        //  observableHorizontalScrollView.setScrollViewListener(this);
        observableHorizontalScrollView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == event.ACTION_UP) {
                    if ((observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6)) && (observableHorizontalScrollView.getScrollX() < (moviesDisplay.getWidth() / 6) * 3)) {
                        scrollCovers(moviesDisplay, moviesDisplay.getWidth() / 3);
                    } else if ((observableHorizontalScrollView.getScrollX() < (moviesDisplay.getWidth() / 6))) {
                        // scrollCovers(moviesDisplay, 0);
                        if (front) {
                            scrollCovers(moviesDisplay, 0);
                        } else {
                            //   reloadMovieCoverLayotBackward();
                            reloadMovieCoverLayot(0, true);
                        }
                    } else if (observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6) * 3) {

                        if (end) {
                            scrollCovers(moviesDisplay, ((moviesDisplay.getWidth() / 6) * 5));
                        } else {
                            //  reloadMovieCoverLayotForward();
                            reloadMovieCoverLayot(((moviesDisplay.getWidth() / 6) * 5), false);
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
            preloadMovieCoverLayot(moviesDisplay, container, inflater);
        }

    }

    boolean center = false;

    private void scrollCovers(LinearLayout moviesDisplay, int scrollValue) {
        ObjectAnimator.ofInt(observableHorizontalScrollView, "scrollX", scrollValue).setDuration(400).start();

    }

    boolean front = true;
    boolean end = false;

    private void reloadMovieCoverLayotForward() {
        ObjectAnimator scrollAnimaition = ObjectAnimator.ofInt(observableHorizontalScrollView, "scrollX", ((moviesDisplay.getWidth() / 6) * 5));
        scrollAnimaition.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewHolder temp = viewHolders[0];
                moviesDisplay.removeView(viewHolders[0].movieCoverLayout);
                viewHolders[0] = viewHolders[1];
                viewHolders[1] = viewHolders[2];
                viewHolders[2] = temp;
                moviesDisplay.addView(temp.movieCoverLayout);


                observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6) * 2, 0);
                front = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        scrollAnimaition.setDuration(400).start();

    }


    private void reloadMovieCoverLayotBackward() {

        ObjectAnimator scrollAnimaition = ObjectAnimator.ofInt(observableHorizontalScrollView, "scrollX", 0);
        scrollAnimaition.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ViewHolder temp = viewHolders[2];
                viewHolders[2] = viewHolders[1];
                viewHolders[1] = viewHolders[0];
                viewHolders[0] = temp;
                moviesDisplay.removeAllViews();
                moviesDisplay.addView(viewHolders[0].movieCoverLayout);
                moviesDisplay.addView(viewHolders[1].movieCoverLayout);
                moviesDisplay.addView(viewHolders[2].movieCoverLayout);


                observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6) * 2, 0);

                end = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        scrollAnimaition.setDuration(400).start();
    }


    private void reloadMovieCoverLayot(int prescrollValue, final boolean isBackward) {

        ObjectAnimator scrollAnimaition = ObjectAnimator.ofInt(observableHorizontalScrollView, "scrollX", prescrollValue);
        scrollAnimaition.addListener(new Animator.AnimatorListener() {
                                         @Override
                                         public void onAnimationStart(Animator animation) {
                                         }

                                         @Override
                                         public void onAnimationEnd(Animator animation) {
                                             if (isBackward) {
                                                 loadCoversBackward();
                                             } else {
                                                 loadCoversForward();
                                             }
                                             observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6) * 2, 0);
                                         }

                                         @Override
                                         public void onAnimationCancel(Animator animation) {
                                         }

                                         @Override
                                         public void onAnimationRepeat(Animator animation) {
                                         }
                                     }

        );
        scrollAnimaition.setDuration(400).start();
    }

    private void loadCoversForward() {
        ViewHolder temp = viewHolders[0];
        moviesDisplay.removeView(viewHolders[0].movieCoverLayout);
        viewHolders[0] = viewHolders[1];
        viewHolders[1] = viewHolders[2];
        viewHolders[2] = temp;
        moviesDisplay.addView(temp.movieCoverLayout);
        front = false;
    }

    private void loadCoversBackward() {
        ViewHolder temp = viewHolders[2];
        viewHolders[2] = viewHolders[1];
        viewHolders[1] = viewHolders[0];
        viewHolders[0] = temp;
        moviesDisplay.removeAllViews();
        moviesDisplay.addView(viewHolders[0].movieCoverLayout);
        moviesDisplay.addView(viewHolders[1].movieCoverLayout);
        moviesDisplay.addView(viewHolders[2].movieCoverLayout);
        end = false;
    }


    private void preloadMovieCoverLayot(LinearLayout moviesDisplay, ViewGroup container, LayoutInflater inflater) {
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
                    if (presentCover < coverHolders.size()) {
                        btn = new Button(getActivity());
//                    btn.setText("kos " + i);
//                    btn.setId(i);
//                        System.out.println("KEY ...........  " + coverHolders.keyAt(presentCover));
//                        System.out.println("HOLDED ...........  " + coverHolders.get(coverHolders.keyAt(presentCover)));
                        System.out.println("id ...........  " + coverHolders.get(coverHolders.keyAt(presentCover)).id);
                        System.out.println("PATH ...........  " + coverHolders.get(coverHolders.keyAt(presentCover)).cover_path);
                        String cover_path = coverHolders.get(coverHolders.keyAt(presentCover)).cover_path;
                        int idm = coverHolders.get(coverHolders.keyAt(presentCover)).id;

                        btn.setText(n + "");
                        btn.setId(idm);


                        System.out.println("2 id ...........  " + coverHolders.get(btn.getId()).id);
                        presentCover++;
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
                                //  System.out.println("layout nr:  " + v.getId());
                                System.out.println("layout nr:  " + coverHolders.get(v.getId()).cover_path);
                                // System.out.println("layout nr:  " + coverHolders.size());


                            }
                        });

                        button_position++;
                        i++;
                    }
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
//        Rect scrollBounds = new Rect();
//
//        observableHorizontalScrollView.getHitRect(scrollBounds);
//
//        if (observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6)) {
//            if (!center) {
//                // reloadMovieCoverLayotForward(moviesDisplay);
//            }
//        }

    }

    @Override
    public void processFinish(Object output) {

        coverHolders = (SparseArray<IdCoverHolder>) output;
        System.out.println("HOLDERS ...........  " + coverHolders);

        loadMovies(moviesDisplay, container, LayoutInflater.from(getContext()));
    }
}