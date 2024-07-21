package com.rj.poc.coraltask.data.room
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BoxDao {
    @Insert
    fun insertBox(box: Box)

    @Query("SELECT * FROM boxes")
     fun getAllBoxes(): List<Box>

    @Query("DELETE FROM boxes")
    fun clearBoxes()
}
