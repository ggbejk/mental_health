import { createRouter, createWebHistory } from 'vue-router'
import { getUserInfo } from '../utils/auth'

import LoginPage from '../views/LoginPage.vue'
import MainLayout from '../layouts/MainLayout.vue'
import StudentHome from '../views/StudentHome.vue'
import CounselorHome from '../views/CounselorHome.vue'
import AdminHome from '../views/AdminHome.vue'
import PsychologistHome from '../views/PsychologistHome.vue'
import PsychologistWarnings from '../views/PsychologistWarnings.vue'
import PsychologistConsultation from '../views/PsychologistConsultation.vue'
import PsychologistSurveys from '../views/PsychologistSurveys.vue'
import PsychologistScales from '../views/PsychologistScales.vue'
import TaskList from '../views/TaskList.vue'
import EmotionDiary from '../views/EmotionDiary.vue'
import EmotionTrend from '../views/EmotionTrend.vue'
import AnonymousConfession from '../views/AnonymousConfession.vue'
import VoiceConfession from '../views/VoiceConfession.vue'
import MyConfessionList from '../views/MyConfessionList.vue'
import CounselorWarningList from '../views/CounselorWarningList.vue'
import CounselorWarningDetail from '../views/CounselorWarningDetail.vue'
import DashboardHome from '../views/DashboardHome.vue'
import SurveyReport from '../views/SurveyReport.vue'
import Questionnaire from '../views/Questionnaire.vue'
import Mindfulness from '../views/Mindfulness.vue'
import BreathingTraining from '../views/BreathingTraining.vue'
import AIChat from '../views/AIChat.vue'
import AdminOperationLog from '../views/AdminOperationLog.vue'
import CounselorDashboard from '../views/CounselorDashboard.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'LoginPage',
    component: LoginPage
  },
  {
    path: '/',
    component: MainLayout,
    children: [
      {
        path: '/student/home',
        name: 'StudentHome',
        component: StudentHome,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/tasks',
        name: 'TaskList',
        component: TaskList,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/emotion/write',
        name: 'EmotionDiary',
        component: EmotionDiary,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/emotion/trend',
        name: 'EmotionTrend',
        component: EmotionTrend,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/confession/write',
        name: 'AnonymousConfession',
        component: AnonymousConfession,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/confession/voice',
        name: 'VoiceConfession',
        component: VoiceConfession,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/confession/my',
        name: 'MyConfessionList',
        component: MyConfessionList,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/mindfulness',
        name: 'Mindfulness',
        component: Mindfulness,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/breathing',
        name: 'BreathingTraining',
        component: BreathingTraining,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/ai-chat',
        name: 'AIChat',
        component: AIChat,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/student/consultation/book',
        name: 'StudentConsultation',
        component: () => import('../views/StudentConsultation.vue'),
        meta: { role: 'STUDENT' }
      },
      {
        path: '/counselor/home',
        name: 'CounselorHome',
        component: CounselorHome,
        meta: { role: 'COUNSELOR' }
      },
      {
        path: '/counselor/warnings',
        name: 'CounselorWarningList',
        component: CounselorWarningList,
        meta: { role: ['COUNSELOR', 'ADMIN'] }
      },
      {
        path: '/counselor/warning/:warningId',
        name: 'CounselorWarningDetail',
        component: CounselorWarningDetail,
        meta: { role: ['COUNSELOR', 'ADMIN'] }
      },
      {
        path: '/counselor/interventions',
        name: 'CounselorInterventions',
        component: () => import('../views/CounselorInterventions.vue'),
        meta: { role: 'COUNSELOR' }
      },
      {
        path: '/counselor/intervention/:interventionId',
        name: 'CounselorInterventionDetail',
        component: () => import('../views/CounselorInterventionDetail.vue'),
        meta: { role: 'COUNSELOR' }
      },
      {
        path: '/counselor/student/:studentId',
        name: 'StudentDetail',
        component: () => import('../views/StudentDetail.vue'),
        meta: { role: 'COUNSELOR' }
      },
      {
        path: '/counselor/students',
        name: 'CounselorStudents',
        component: () => import('../views/CounselorStudents.vue'),
        meta: { role: 'COUNSELOR' }
      },
      {
        path: '/admin/home',
        name: 'AdminHome',
        component: AdminHome,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/home',
        name: 'PsychologistHome',
        component: PsychologistHome,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/warnings',
        name: 'PsychologistWarnings',
        component: PsychologistWarnings,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/consultation',
        name: 'PsychologistConsultation',
        component: PsychologistConsultation,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/consultation/:consultationId',
        name: 'ConsultationDetail',
        component: () => import('../views/ConsultationDetail.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/consultation/:consultationId/schedule',
        name: 'ConsultationSchedule',
        component: () => import('../views/ConsultationSchedule.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/surveys',
        name: 'PsychologistSurveys',
        component: PsychologistSurveys,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/survey/create',
        name: 'SurveyCreate',
        component: () => import('../views/SurveyCreate.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/survey/:taskId',
        name: 'SurveyDetail',
        component: () => import('../views/SurveyDetail.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/scales',
        name: 'PsychologistScales',
        component: PsychologistScales,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/scale/create',
        name: 'ScaleCreate',
        component: () => import('../views/ScaleCreate.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/scale/:id/edit',
        name: 'ScaleEdit',
        component: () => import('../views/ScaleEdit.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/scale/:id',
        name: 'ScaleView',
        component: () => import('../views/ScaleEdit.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/psychologist/evaluate/:id',
        name: 'PsychologistEvaluate',
        component: () => import('../views/PsychologistEvaluate.vue'),
        meta: { role: 'ADMIN' }
      },
      {
        path: '/student/report',
        name: 'SurveyReport',
        component: SurveyReport,
        meta: { role: 'STUDENT' }
      },
      {
        path: '/dashboard',
        name: 'DashboardHome',
        component: DashboardHome,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/counselor/dashboard',
        name: 'CounselorDashboard',
        component: CounselorDashboard,
        meta: { role: 'COUNSELOR' }
      },
      {
        path: '/admin/operation-log',
        name: 'AdminOperationLog',
        component: AdminOperationLog,
        meta: { role: 'ADMIN' }
      },
      {
        path: '/student/task/:taskId',
        name: 'Questionnaire',
        component: Questionnaire,
        meta: { role: 'STUDENT' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})
router.beforeEach((to) => {
  const user = getUserInfo()

  if (to.path === '/login') {
    return true
  }

  if (!user) {
    return '/login'
  }

  const needRole = to.meta.role
  if (!needRole) {
    return true
  }

  if (Array.isArray(needRole)) {
    return needRole.includes(user.role) ? true : '/login'
  }

  return user.role === needRole ? true : '/login'
})

export default router