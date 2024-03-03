package com.andreev.ocrbackend.core.service.domain.security

import com.andreev.ocrbackend.core.model.User
import com.andreev.ocrbackend.core.model.security.RoleName
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrinciple(
    private val email: String,
    private val password: String,
    private val authorities: MutableCollection<out GrantedAuthority>,
) : UserDetails {

    companion object {
        fun build(user: User): UserPrinciple {
            val authorities = mutableListOf<GrantedAuthority>(SimpleGrantedAuthority(RoleName.ROLE_USER.name))

            return UserPrinciple(
                email = user.email,
                password = user.password,
                authorities = authorities
            )
        }
    }

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return email
    }

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true
}