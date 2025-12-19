package ma.projet.soapclient.ws

import ma.projet.soapclient.beans.Compte
import ma.projet.soapclient.beans.TypeCompte
import org.ksoap2.SoapEnvelope
import org.ksoap2.serialization.SoapObject
import org.ksoap2.serialization.SoapSerializationEnvelope
import org.ksoap2.transport.HttpTransportSE
import java.text.SimpleDateFormat
import java.util.*

class Service {
    private val NAMESPACE = "http://ws.soapAcount/"
    private val URL = "http://10.0.2.2:8082/services/ws"
    private val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())

    fun getComptes(): List<Compte> {
        val comptes = mutableListOf<Compte>()
        val request = SoapObject(NAMESPACE, "getComptes")
        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.setOutputSoapObject(request)
        val transport = HttpTransportSE(URL)

        try {
            transport.call(null, envelope)
            val response = envelope.response
            if (response is SoapObject) {
                for (i in 0 until response.propertyCount) {
                    val item = response.getProperty(i) as SoapObject
                    comptes.add(parseCompte(item))
                }
            } else if (response is Vector<*>) {
                for (i in 0 until response.size) {
                    val item = response[i] as SoapObject
                    comptes.add(parseCompte(item))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return comptes
    }

    fun createCompte(solde: Double, type: String): Boolean {
        val request = SoapObject(NAMESPACE, "createCompte")
        request.addProperty("solde", solde)
        request.addProperty("type", type)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.setOutputSoapObject(request)
        val transport = HttpTransportSE(URL)

        return try {
            transport.call(null, envelope)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun deleteCompte(id: Long): Boolean {
        val request = SoapObject(NAMESPACE, "deleteCompte")
        request.addProperty("id", id)

        val envelope = SoapSerializationEnvelope(SoapEnvelope.VER11)
        envelope.setOutputSoapObject(request)
        val transport = HttpTransportSE(URL)

        return try {
            transport.call(null, envelope)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun parseCompte(soapObject: SoapObject): Compte {
        val id = soapObject.getPropertySafeAsString("id").toLongOrNull()
        val solde = soapObject.getPropertySafeAsString("solde").toDoubleOrNull() ?: 0.0
        val dateStr = soapObject.getPropertySafeAsString("dateCreation")
        val typeStr = soapObject.getPropertySafeAsString("type")
        
        val date = try {
            sdf.parse(dateStr) ?: Date()
        } catch (e: Exception) {
            Date()
        }
        
        val type = try {
            TypeCompte.valueOf(typeStr)
        } catch (e: Exception) {
            TypeCompte.COURANT
        }

        return Compte(id, solde, date, type)
    }

    private fun SoapObject.getPropertySafeAsString(name: String): String {
        return try {
            getProperty(name)?.toString() ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
