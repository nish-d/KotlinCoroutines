package com.raywenderlich.kotlin.coroutines.ui.movies

import android.util.Log
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.raywenderlich.kotlin.coroutines.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

/**
 * Created by nishita.dutta on 2020-03-23, 11:42.
 */
@RunWith(PowerMockRunner::class)
@PrepareForTest(Log::class)
class MoviesPresenterImplTest {

    private val testDispatcher = TestCoroutineDispatcher()
    private val testCoroutineScope = TestCoroutineScope(testDispatcher)
    private val repository  = mock<MovieRepository>()
    private val view = mock<MoviesView>()

    private val presenter by lazy {
        MoviesPresenterImpl(repository)
    }

    @Before
    fun setUp(){
        PowerMockito.mockStatic(Log::class.java)
        Dispatchers.setMain(testDispatcher)

        presenter.setView(view)
    }

    @Test
    fun getDataShowsResult()  = testCoroutineScope.runBlockingTest{
        //arrange
        whenever(repository.getMovies()).thenReturn(listOf())

        //act
        presenter.getData()

        //asset
        verify(repository).getMovies()
        verify(view).showMovies(any())
    }
}