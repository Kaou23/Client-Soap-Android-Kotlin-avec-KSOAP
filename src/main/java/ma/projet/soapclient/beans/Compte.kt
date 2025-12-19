package ma.projet.soapclient.beans

import java.util.Date

data class Compte(
    var id: Long? = null,
    var solde: Double = 0.0,
    var dateCreation: Date = Date(),
    var type: TypeCompte = TypeCompte.COURANT
)
