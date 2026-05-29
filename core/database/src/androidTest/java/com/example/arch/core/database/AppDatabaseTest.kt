package com.example.arch.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.arch.core.database.dao.PostDao
import com.example.arch.core.database.entity.PostEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppDatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var postDao: PostDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        postDao = db.postDao()
    }

    @After
    fun tearDown() { db.close() }

    @Test
    fun insertAndRetrieve_posts() = runTest {
        val post = PostEntity(id = "1", title = "Hello", body = "World", userId = "u1", createdAt = 0L)
        postDao.insertAll(listOf(post))
        val posts = postDao.getAllPosts().first()
        assertThat(posts).hasSize(1)
        assertThat(posts.first().title).isEqualTo("Hello")
    }

    @Test
    fun delete_post_removes_it() = runTest {
        val post = PostEntity(id = "2", title = "Delete me", body = "", userId = "u1", createdAt = 0L)
        postDao.insertAll(listOf(post))
        postDao.deleteAll()
        val posts = postDao.getAllPosts().first()
        assertThat(posts).isEmpty()
    }
}
