/*
 * Copyright (c) 2019 Razeware LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * Notwithstanding the foregoing, you may not use, copy, modify, merge, publish,
 * distribute, sublicense, create a derivative work, and/or sell copies of the
 * Software in any work that is designed, intended, or marketed for pedagogical or
 * instructional purposes related to programming, coding, application development,
 * or information technology.  Permission for such use, copying, modification,
 * merger, publication, distribution, sublicensing, creation of derivative works,
 * or sale is expressly withheld.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.raywenderlich.kotlin.coroutines.domain.repository

import com.raywenderlich.kotlin.coroutines.data.api.MovieApiService
import com.raywenderlich.kotlin.coroutines.data.database.MovieDao
import com.raywenderlich.kotlin.coroutines.di.API_KEY
import com.raywenderlich.kotlin.coroutines.data.model.Movie
import com.raywenderlich.kotlin.coroutines.data.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.IOException

/**
 * Connects to the end entity, and exposes functionality to the user.
 */
class MovieRepositoryImpl(
    private val movieApiService: MovieApiService,
    private val movieDao: MovieDao
) : MovieRepository {

  override suspend fun getMovies(): Result<out List<Movie>> = withContext(Dispatchers.IO){

    //fetch from database and network paralelly
    val cachedMoviesDeffered = async { movieDao.getSavedMovies() }
    val resultDeffered = async { movieApiService.getMovies(API_KEY).execute() }

    //this code will wait until cachedMoviesDeffered has a result. It is not necessary for resultDeferred to have completed by now
    val cachedMovies = cachedMoviesDeffered.await()

    try{
      //this code will wait until resultDeffered has a result.
      val result = resultDeffered.await()


      //Hence we can save time by running two separate unrelated pieces of code parallely and waiting for them in the parent coroutine only when we know
      //we will be needing them
      val moviesRespone =  result.body()?.movies

      if(result.isSuccessful && !moviesRespone.isNullOrEmpty()){
        Result(moviesRespone,null)
      }else{
        Result(cachedMovies, null)
      }

    }catch (e : Throwable){
      if (e is IOException && cachedMovies.isEmpty()){
        Result(null, e)
      }else{
        Result(cachedMovies,null)
      }
    }
  }
}