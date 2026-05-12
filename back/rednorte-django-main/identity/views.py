from django.contrib.auth.models import Group, User
from rest_framework import status
from rest_framework.decorators import api_view, permission_classes
from rest_framework.permissions import AllowAny, IsAuthenticated, IsAdminUser
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


def _is_admin_role(user) -> bool:
    return user.is_superuser or user.groups.filter(name="admin").exists()


@api_view(["POST"])
@permission_classes([AllowAny])
def register_patient(request):
    serializer = PatientRegisterSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    user = serializer.save()

    group, _ = Group.objects.get_or_create(name="patient")
    user.groups.clear()
    user.groups.add(group)

    return Response(
        {"message": "Patient registered", "user_id": user.id},
        status=status.HTTP_201_CREATED,
    )


@api_view(["POST"])
@permission_classes([IsAuthenticated])
def register_doctor(request):
    if not _is_admin_role(request.user):
        return Response({"detail": "Not authorized."}, status=status.HTTP_403_FORBIDDEN)

    serializer = DoctorRegisterSerializer(data=request.data)
    serializer.is_valid(raise_exception=True)
    user = serializer.save()

    return Response(
        {"message": "Doctor registered", "user_id": user.id},
        status=status.HTTP_201_CREATED,
    )


@api_view(["POST"])
@permission_classes([IsAuthenticated])
def change_password(request):
    serializer = ChangePasswordSerializer(
        data=request.data,
        context={"request": request},
    )
    serializer.is_valid(raise_exception=True)
    serializer.save()

    return Response({"message": "Password updated"}, status=status.HTTP_200_OK)


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


@api_view(["GET"])
@permission_classes([IsAdminUser])
def list_users(request):
    users = User.objects.all().order_by("id")
    data = []

    for user in users:
        profile = getattr(user, "profile", None)

        data.append(
            {
                "id": user.id,
                "username": user.username,
                "email": user.email,
                "is_staff": user.is_staff,
                "is_superuser": user.is_superuser,
                "is_active": user.is_active,
                "roles": list(user.groups.values_list("name", flat=True)),
                "profile": {
                    "rut": profile.rut if profile else "",
                    "nombres": profile.nombres if profile else "",
                    "apellidos": profile.apellidos if profile else "",
                    "telefono": profile.telefono if profile else "",
                    "genero": profile.genero if profile else "",
                    "fecha_nacimiento": str(profile.fecha_nacimiento) if profile else "",
                    "especialidad": profile.especialidad if profile else "",
                }
                if profile
                else None,
            }
        )

    return Response(data)


@api_view(["PUT"])
@permission_classes([IsAdminUser])
def update_user(request, user_id):
    try:
        user = User.objects.get(id=user_id)
    except User.DoesNotExist:
        return Response(
            {"detail": "Usuario no encontrado."},
            status=status.HTTP_404_NOT_FOUND,
        )

    if user.is_superuser:
        return Response(
            {"detail": "No se puede modificar un superusuario desde el frontend."},
            status=status.HTTP_403_FORBIDDEN,
        )

    profile = getattr(user, "profile", None)

    mail = request.data.get("mail")
    if mail:
        exists = User.objects.filter(email__iexact=mail).exclude(id=user.id).exists()
        if exists:
            return Response(
                {"mail": "Email already exists."},
                status=status.HTTP_400_BAD_REQUEST,
            )
        user.email = mail.lower().strip()

    if "is_active" in request.data:
        user.is_active = bool(request.data.get("is_active"))

    user.save()

    if profile:
        profile.nombres = request.data.get("nombres", profile.nombres)
        profile.apellidos = request.data.get("apellidos", profile.apellidos)
        profile.telefono = request.data.get("telefono", profile.telefono)
        profile.genero = request.data.get("genero", profile.genero)

        if "especialidad" in request.data:
            profile.especialidad = request.data.get("especialidad")

        profile.save()

    role = request.data.get("role")

    if role:
        if role not in ["patient", "doctor"]:
            return Response(
                {"role": "Rol no permitido. Use patient o doctor."},
                status=status.HTTP_400_BAD_REQUEST,
            )

        user.groups.clear()
        group, _ = Group.objects.get_or_create(name=role)
        user.groups.add(group)

    return Response({"message": "Usuario actualizado correctamente."})

@api_view(["DELETE"])
@permission_classes([IsAdminUser])
def delete_user(request, user_id):
    try:
        user = User.objects.get(id=user_id)
    except User.DoesNotExist:
        return Response(
            {"detail": "Usuario no encontrado."},
            status=status.HTTP_404_NOT_FOUND,
        )

    if user.is_superuser:
        return Response(
            {"detail": "No se puede eliminar un superusuario."},
            status=status.HTTP_403_FORBIDDEN,
        )

    if user == request.user:
        return Response(
            {"detail": "No puedes eliminar tu propio usuario."},
            status=status.HTTP_403_FORBIDDEN,
        )

    user.delete()

    return Response(
        {"message": "Usuario eliminado correctamente."},
        status=status.HTTP_200_OK,
    )