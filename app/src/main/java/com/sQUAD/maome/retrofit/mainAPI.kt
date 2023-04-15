package com.sQUAD.maome.retrofit

import com.sQUAD.maome.retrofit.auth.AuthRequest
import com.sQUAD.maome.retrofit.auth.RegisterRequest
import com.sQUAD.maome.retrofit.auth.RegisterResponse
import com.sQUAD.maome.retrofit.main.Note
import com.sQUAD.maome.retrofit.main.NoteCreateRequest
import com.sQUAD.maome.retrofit.main.Notes
import com.sQUAD.maome.retrofit.main.Photo
import retrofit2.Response
import retrofit2.http.*

interface MainApi {

    @POST("auth/signin")
    suspend fun auth(@Body authRequest: AuthRequest): Response<User> // return user

    @POST("auth/signup")
    suspend fun register(@Body registerRequest: RegisterRequest): Response<RegisterResponse> // return server response(string format)

    @POST("notes/photo/upload")
    suspend fun photoUpload(
        @Header("Authorization") token: String,
        @Body photo: Photo
    ): Response<Long> // return users photo id

    @POST("notes/create")
    suspend fun createNote(
        @Header("Authorization") token: String,
        @Body noteCreateRequest: NoteCreateRequest
    ): Response<Long> // return users note id

    @POST("notes/edit")
    suspend fun editNote(
        @Header("Authorization") token: String,
        @Body note: Note
    ): Response<Long> // return users note edited id

    @GET("notes/photo/get/{id}")
    suspend fun getPhotoById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<ByteArray> // return bytearray(photo)

    @GET("notes/all")
    suspend fun getAllNotes(@Header("Authorization") token: String): Response<Notes> // return array of notes

    @GET("notes/read/{id}")
    suspend fun getSingleNoteById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ): Response<Note> // return users note

    @POST("notes/delete/{id}")
    suspend fun deleteNoteById(
        @Header("Authorization") token: String,
        @Path("id") id: Long
    ) // return nothing

    @POST("notes/removeAdd/note_id/{note_id}/photo_id/{photo_id}")
    suspend fun removePhotoFromNote(
        @Header("Authorization") token: String,
        @Path("note_id") note_id: Long,
        @Path("photo_id") photo_id: Long
    ): Response<Note> // return updated note

    @POST("notes/photoAdd/note_id/{note_id}/photo_id/{photo_id}")
    suspend fun addPhotoToNote(
        @Header("Authorization") token: String,
        @Path("note_id") note_id: Long,
        @Path("photo_id") photo_id: Long
    ): Response<Note> // return updated note
}