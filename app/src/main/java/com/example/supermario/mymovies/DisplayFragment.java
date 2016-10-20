package com.example.supermario.mymovies;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.supermario.mymovies.extra.ObservableHorizontalScrollView;
import com.example.supermario.mymovies.util.AsyncResponse;
import com.example.supermario.mymovies.util.IdCoverHolder;
import com.example.supermario.mymovies.util.MoviesCoversDAO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.example.supermario.mymovies.R.id.coversScrollView;
import static com.example.supermario.mymovies.R.id.miniDaysLayout;

public class DisplayFragment extends Fragment implements AsyncResponse {

    private boolean init = true;
    // private ArrayAdapter<String> coversAdapter;
    private boolean preloaded = false;
    private ViewHolder viewHolders[];
    private ObservableHorizontalScrollView observableHorizontalScrollView;
    private LinearLayout moviesDisplay;
    LinkedHashMap<Integer, IdCoverHolder> coverHolders;
    List<Integer> coverHoldersList;
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

                            reloadMovieCoverLayot(0, true);
                        }
                    } else if (observableHorizontalScrollView.getScrollX() > (moviesDisplay.getWidth() / 6) * 3) {

                        if (end) {
                            scrollCovers(moviesDisplay, ((moviesDisplay.getWidth() / 6) * 5));
                        } else {

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
                                           //  observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6) * 2, 0);
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


    private void loadCoverAndIncrement(ImageView img) {
        if (presentCover < coverHolders.size()) {
            Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185" + coverHolders.get(coverHoldersList.get(presentCover)).cover_path).into(img);
            img.setId(coverHolders.get(coverHoldersList.get(presentCover)).id);
            //  img.setId(coverHolders.keyAt(presentCover));
            presentCover++;
        }
    }


    private void loadCoversForward() {

        if ((presentCover + 1 + 4) >= coverHolders.size()) {
            pageNumber++;
         //   presentCover++;
            MoviesCoversDAO moviesCoversDAO = new MoviesCoversDAO();
            moviesCoversDAO.asyncResponse = this;
            moviesCoversDAO.execute(MoviesCoversDAO.SEARCH_POPULAR, String.valueOf(pageNumber));
        } else {
            for (int i = 0; i < 4; i++) {
                loadCoverAndIncrement(viewHolders[0].buttons[i]);
            }

            ViewHolder temp = viewHolders[0];
            moviesDisplay.removeView(viewHolders[0].movieCoverLayout);
            viewHolders[0] = viewHolders[1];
            viewHolders[1] = viewHolders[2];
            viewHolders[2] = temp;
            moviesDisplay.addView(temp.movieCoverLayout);
            front = false;
            observableHorizontalScrollView.scrollTo((moviesDisplay.getWidth() / 6) * 2, 0);
        }

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


        int width = this.getResources().getDisplayMetrics().widthPixels;
        int rowCapacity = getRowCapacity();


        ViewHolder viewHolder;


        for (int n = 0; n < 3; n++) {
            viewHolder = new ViewHolder();
            viewHolder.buttons = new ImageButton[rowCapacity * 2];
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
                ImageButton btn;
                for (int s = 0; s < rowCapacity; s++) {
                    if (presentCover < coverHolders.size()) {
                        btn = new ImageButton(getActivity());
                        //int idm = coverHolders.get(coverHolders.keyAt(presentCover)).id;
                        int idm = coverHolders.get(coverHoldersList.get(presentCover)).id;


                        // coverHoldersList.get(i)
                        btn.setId(idm);

                        Picasso.with(getContext()).load("http://image.tmdb.org/t/p/w185" + coverHolders.get(coverHoldersList.get(presentCover)).cover_path).into(btn);

                        presentCover++;
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT
                        );
                        lp.weight = 1.0f;

                        btn.setLayoutParams(lp);
                        btn.setPadding(0, 0, 0, 0);
                        btn.setScaleType(ImageView.ScaleType.FIT_XY);
                        row.addView(btn);
                        viewHolder.buttons[button_position] = btn;
                        LinearLayout tmp = (LinearLayout) btn.getParent().getParent();

                        //     final int id = tmp.getId();
                        btn.setOnClickListener(new android.view.View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                // TODO Auto-generated method stub
                                System.out.println("layout nr:  " + v.getId());
                                System.out.println("layout nr:  " + coverHolders.get(v.getId()).id);
                            }
                        });

                        button_position++;

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
        public ImageButton[] buttons;
    }


    @Override
    public void processFinish(Map<Integer, IdCoverHolder> output) {


        // System.out.println("HOLDERS ...........  " + coverHolders);
        if (init) {
            init = false;
            coverHolders = new LinkedHashMap(output);
            coverHoldersList = new ArrayList<Integer>(output.keySet());

            loadMovies(moviesDisplay, container, LayoutInflater.from(getContext()));


        } else {
            // System.out.println("COVER HOLDERS BEFORE UPDATE SIZE:............................ " + coverHolders.size());
//            for (int s = 0; s < coverHolders.size(); s++) {
//                System.out.println(s + " COVER HOLDER : " + coverHolders.get(coverHoldersList.get(s)).id);
//            }

            if (coverHolders == null) {
                return;
            }
            if (coverHolders.size() > 20) {
                for (int i = 0; i < output.size(); i++) {
                    // coverHolders.remove(coverHolders.keyAt(0));
//                    System.out.println("!!!!!");
                    coverHolders.remove(coverHoldersList.get(0));
                    coverHoldersList.remove(0);
                    presentCover--;
                }
            } else {
                for (int x = 0; x < 4; x++) {
                    //  System.out.println(x + " REMOVE: " + coverHolders.keyAt(0));
                    //coverHolders.remove(coverHolders.keyAt(0));
//                    System.out.println("REMOVING ..." + coverHolders.get(coverHoldersList.get(0)).id + "    " + coverHoldersList.get(0));
                    coverHolders.remove(coverHoldersList.get(0));
                    coverHoldersList.remove(0);
                    presentCover--;

                }
                //   System.out.println("COVER AFTER REMOVE: " + coverHolders.size());
            }
            //System.out.println("COVER AFTER REMOVE: ........................" + coverHolders.size());

//            for (int s = 0; s < coverHolders.size(); s++) {
//                System.out.println(s + " COVER HOLDER : " + coverHoldersList.get(s));
//                System.out.println(s + " COVER HOLDER : " + coverHolders.get(coverHoldersList.get(s)));
//                System.out.println(s + " COVER HOLDER : " + coverHolders.get(coverHoldersList.get(s)).id);
//            }

            //   remove from coverHoldersList
            coverHoldersList.addAll(output.keySet());

         //   System.out.println("COVER LIST AFTER ADD: .............................." + coverHolders.size());

//            for (int i = 0; i < coverHoldersList.size(); i++) {
//                System.out.println(i + " COVER HOLDER L: " + coverHoldersList.get(i));
//            }
            coverHolders.putAll(output);


            //  coverHolders.put(output.keyAt(i), output.get(output.keyAt(i)));
//            int g = 0;
//            for (Integer key : coverHolders.keySet()) {
//                System.out.println(g + " COVER HOLDER KEY: " + key);
//                g++;
//            }

          //  System.out.println("COVER HOLDERS AFTER UPDATE SIZE:...................... " + coverHolders.size());
          //  System.out.println("COVER HOLDERS list AFTER UPDATE SIZE: ....................." + coverHoldersList.size());
            //  System.out.println("presentCover:  " + presentCover);

//            for (int i = 0; i < coverHolders.size(); i++) {
//                System.out.println(i + " OUTPUT : " + coverHoldersList.get(i));
//                System.out.println(i + " OUTPUT : " + coverHolders.get(coverHoldersList.get(i)).id);
//
//            }
//            System.out.println(" present cover: ...................." + presentCover);
            loadCoversForward();


        }

    }
}