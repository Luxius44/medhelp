package com.example.data

import java.io.Serializable

data class ProfilData(
	var age: Int? = null,
	var sexe: String? = null,
	var taille: Int? = null,
	var poids: Int? = null,
	var allergie: String? = null,
	var chronique: String? = null,
	var complement: String? = null,
) : Serializable
