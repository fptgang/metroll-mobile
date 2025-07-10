package com.vidz.data.datasource.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vidz.domain.model.FirebaseTicket
import com.vidz.domain.model.FirebaseTicketStatus
import com.vidz.domain.model.TicketType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTicketDataSource @Inject constructor() {
    
    private val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance("https://metroll-bbda2-default-rtdb.asia-southeast1.firebasedatabase.app/")
    }
    
    private val ticketsRef: DatabaseReference by lazy {
        database.getReference("tickets")
    }
    
    suspend fun getTicketStatus(ticketId: String): Flow<FirebaseTicket?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    if (snapshot.exists()) {
                        val ticketData = snapshot.value as? Map<String, Any>
                        
                        val firebaseTicket = FirebaseTicket(
                            ticketId = ticketData?.get("ticketId") as? String ?: ticketId,
                            ticketType = when (ticketData?.get("ticketType") as? String) {
                                "TIMED" -> TicketType.TIMED
                                "P2P" -> TicketType.P2P
                                else -> TicketType.P2P
                            },
                            status = when (ticketData?.get("status") as? String) {
                                "VALID" -> FirebaseTicketStatus.VALID
                                "IN_USED" -> FirebaseTicketStatus.IN_USED
                                "USED" -> FirebaseTicketStatus.USED
                                "EXPIRED" -> FirebaseTicketStatus.EXPIRED
                                "CANCELLED" -> FirebaseTicketStatus.CANCELLED
                                else -> FirebaseTicketStatus.VALID
                            },
                            validUntil = ticketData?.get("validUntil") as? String ?: "",
                            startStationId = ticketData?.get("startStationId") as? String,
                            endStationId = ticketData?.get("endStationId") as? String
                        )
                        
                        trySend(firebaseTicket)
                    } else {
                        trySend(null)
                    }
                } catch (e: Exception) {
                    trySend(null)
                }
            }
            
            override fun onCancelled(error: DatabaseError) {
                trySend(null)
                close(error.toException())
            }
        }
        
        ticketsRef.child(ticketId).addValueEventListener(listener)
        
        awaitClose {
            ticketsRef.child(ticketId).removeEventListener(listener)
        }
    }
    
    suspend fun updateTicketStatus(ticketId: String, status: FirebaseTicketStatus): Flow<Boolean> = callbackFlow {
        val updates = mapOf("status" to status.name)
        
        ticketsRef.child(ticketId).updateChildren(updates)
            .addOnSuccessListener {
                trySend(true)
                close()
            }
            .addOnFailureListener { exception ->
                trySend(false)
                close(exception)
            }
        
        awaitClose { }
    }
} 