from django.urls import path
from rest_framework_simplejwt.views import TokenObtainPairView, TokenRefreshView

from . import views

urlpatterns = [
    path("health/", views.health, name="health"),
    path("auth/register/patient/", views.register_patient, name="register-patient"),
    path("auth/register/doctor/", views.register_doctor, name="register-doctor"),
    path("auth/change-password/", views.change_password, name="change-password"),
    path("auth/token/", TokenObtainPairView.as_view(), name="token_obtain_pair"),
    path("auth/token/refresh/", TokenRefreshView.as_view(), name="token_refresh"),
    path("me/", views.me, name="me"),
]