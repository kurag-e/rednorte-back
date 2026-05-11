from django.contrib.auth.models import Group
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated
from rest_framework.response import Response

from .serializers import (
    ChangePasswordSerializer,
    DoctorRegisterSerializer,
    PatientRegisterSerializer,
)


@api_view(["GET"])
@permission_classes([AllowAny])
def health(request):
    return Response({"status": "ok"})


@api_view(["POST"])
@permission_classes([AllowAny])
def register_patient(request):
    serializer = PatientRegisterSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    user = serializer.save()

    group, _ = Group.objects.get_or_create(name="patient")
    user.groups.clear()
    user.groups.add(group)

    return Response({"message": "Patient registered", "user_id": user.id}, status=201)


def _is_admin_role(user) -> bool:
    return user.is_superuser or user.groups.filter(name="admin").exists()


@api_view(["POST"])
@permission_classes([IsAuthenticated])
def register_doctor(request):
    if not _is_admin_role(request.user):
        return Response({"detail": "Not authorized."}, status=403)

    serializer = DoctorRegisterSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    user = serializer.save()

    return Response({"message": "Doctor registered", "user_id": user.id}, status=201)


@api_view(["POST"])
@permission_classes([IsAuthenticated])
def change_password(request):
    serializer = ChangePasswordSerializer(data=request.data, context={"request": request})
    serializer.is_valid(raise_exception=True)
    serializer.save()
    return Response({"message": "Password updated"}, status=200)


@api_view(["GET"])
@permission_classes([IsAuthenticated])
def me(request):
    user = request.user
    roles = list(user.groups.values_list("name", flat=True))

    profile = getattr(user, "profile", None)
    profile_data = None
    if profile:
        profile_data = {
            "rut": profile.rut,
            "nombres": profile.nombres,
            "apellidos": profile.apellidos,
            "genero": profile.genero,
            "telefono": profile.telefono,
            "fecha_nacimiento": str(profile.fecha_nacimiento),
            "especialidad": profile.especialidad,
            "nombre_tutor": profile.nombre_tutor,
            "telefono_tutor": profile.telefono_tutor,
        }

    return Response(
        {
            "id": user.id,
            "rut_login": user.username,
            "email": user.email,
            "roles": roles,
            "is_staff": user.is_staff,
            "is_superuser": user.is_superuser,
            "profile": profile_data,
        }
    )
# Create your views here.
