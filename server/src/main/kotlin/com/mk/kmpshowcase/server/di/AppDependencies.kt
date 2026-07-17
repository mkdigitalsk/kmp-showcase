package com.mk.kmpshowcase.server.di

import com.mk.kmpshowcase.server.core.mail.Mailer
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.server.core.mail.ResendMailer
import com.mk.kmpshowcase.server.core.mail.SmtpMailer
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadRepository
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadRepositoryImpl
import com.mk.kmpshowcase.server.feature.lead.service.LeadService
import com.mk.kmpshowcase.server.feature.note.persistence.NoteRepository
import com.mk.kmpshowcase.server.feature.note.persistence.NoteRepositoryImpl
import com.mk.kmpshowcase.server.feature.note.service.NoteService
import com.mk.kmpshowcase.server.feature.project.persistence.ProjectRepository
import com.mk.kmpshowcase.server.feature.project.persistence.ProjectRepositoryImpl
import com.mk.kmpshowcase.server.feature.project.service.ProjectService
import com.mk.kmpshowcase.server.feature.user.persistence.InviteRepository
import com.mk.kmpshowcase.server.feature.user.persistence.InviteRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.service.InviteService
import com.mk.kmpshowcase.server.feature.user.service.UserService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

internal class AppDependencies(
    val jwtConfig: JwtConfig,
    mailConfig: MailConfig,
    portalBaseUrl: String = "https://mkdigital.sk",
) {

    private val userRepository: UserRepository = UserRepositoryImpl()
    private val inviteRepository: InviteRepository = InviteRepositoryImpl()
    private val noteRepository: NoteRepository = NoteRepositoryImpl()
    private val leadRepository: LeadRepository = LeadRepositoryImpl()
    private val projectRepository: ProjectRepository = ProjectRepositoryImpl()
    private val mailer: Mailer =
        if (mailConfig.resendApiKey.isNotBlank()) ResendMailer(mailConfig) else SmtpMailer(mailConfig)
    private val mailScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val userService = UserService(userRepository)
    val inviteService = InviteService(inviteRepository, userRepository, userService, mailer, portalBaseUrl, mailScope)
    val noteService = NoteService(noteRepository)
    val leadService = LeadService(leadRepository, mailer, mailConfig.recipient, mailScope)
    val projectService = ProjectService(projectRepository)
}
