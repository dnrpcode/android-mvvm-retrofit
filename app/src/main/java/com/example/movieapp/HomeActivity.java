package com.example.movieapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import com.example.movieapp.adapters.GenreRecycleView;
import com.example.movieapp.adapters.MovieRecycleView;
import com.example.movieapp.adapters.OnMovieListener;
import com.example.movieapp.models.GenreModel;
import com.example.movieapp.models.MovieModel;
import com.example.movieapp.models.viewModels.GenreListViewModel;
import com.example.movieapp.models.viewModels.PopularMovieListViewModel;
import com.example.movieapp.models.viewModels.SearchMovieListViewModel;

public class HomeActivity extends AppCompatActivity implements OnMovieListener {

    private GenreListViewModel genreListViewModel;
    private RecyclerView recyclerView, recyclerViewPopular;
    private GenreRecycleView recycleViewAdapter;

    private PopularMovieListViewModel popularMovieListViewModel;
    private SearchMovieListViewModel searchMovieListViewModel;
    private MovieRecycleView movieViewAdapter;

    private boolean isPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recyclerView = findViewById(R.id.categories);
        recyclerViewPopular = findViewById(R.id.populars);

        genreListViewModel = new ViewModelProvider(this).get(GenreListViewModel.class);
        popularMovieListViewModel = new ViewModelProvider(this).get(PopularMovieListViewModel.class);
        searchMovieListViewModel = new ViewModelProvider(this).get(SearchMovieListViewModel.class);

        // search setup
        searchSetup();

        // data observer
        ObservasingAnyChangesPopularMovie();
        ObservasingAnyChangesSearchMovie();

        // show popular movies
        popularMovieListViewModel.getPopularMovie(1);

        // configuring recyclerview
        configureRecycleView();

        // data observer
        ObservasingAnyChangesGenres();

        // show popular movies
        genreListViewModel.getGenres();

        // configuring recyclerview
        configureRecycleViewPopular();
    }

    // init recycleView and adding data to it
    private void configureRecycleView() {
        recycleViewAdapter = new GenreRecycleView();
        recyclerView.setAdapter(recycleViewAdapter);
    }


    private void ObservasingAnyChangesGenres() {
        genreListViewModel.getGenre().observe(this, GenreModels -> {
            // observing any data changes
            if (GenreModels != null) {
                for (GenreModel model : GenreModels) {
                    // get data
                    recycleViewAdapter.setGenre(GenreModels);
                }
            }
        });
    }

    private void searchSetup() {
        final SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchMovieListViewModel.getSearchMovie(query, 1);
                isPopular = false;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchView.setOnClickListener(view -> isPopular = false);
    }

    // init recycleView and adding data to it
    private void configureRecycleViewPopular() {
        movieViewAdapter = new MovieRecycleView(this);
        recyclerViewPopular.setAdapter(movieViewAdapter);

        // pagination & loading nect results
        recyclerViewPopular.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerViewPopular.canScrollVertically(1)) {
                    // here display next result
//                    searchMovieListViewModel.searchMovieNextPage();
//                    if (isPopular) {
                    popularMovieListViewModel.popularMovieNextPage();
//                    }
                }
            }
        });
    }

    private void ObservasingAnyChangesSearchMovie() {
        searchMovieListViewModel.getSearchMovie().observe(this, movieModels -> {
            // observing any data changes
            if (movieModels != null) {
                for (MovieModel model : movieModels) {
                    // get data
                    movieViewAdapter.setMovie(movieModels);
                }
            }
        });
    }

    private void ObservasingAnyChangesPopularMovie() {
        popularMovieListViewModel.getPopularMovie().observe(this, movieModels -> {
            // observing any data changes
            if (movieModels != null) {
                for (MovieModel model : movieModels) {
                    // get data
                    movieViewAdapter.setMovie(movieModels);
                }
            }
        });
    }

    @Override
    public void onMovieClick(int pos) {
        // here is going to detail movie that has clicked
        Intent intent = new Intent(this, DetailMovieActivity.class);
        intent.putExtra("movie", movieViewAdapter.getSelectedMovie(pos));
        startActivity(intent);
    }
}