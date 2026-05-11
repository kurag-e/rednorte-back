import re
from datetime import date

from django.contrib.auth.models import Group, User
from rest_framework import serializers

from .models import Profile


def clean_rut(rut: str) -> str:
    return rut.replace(".", "").replace("-", "").strip().upper()


def validate_rut_dv(rut: str) -> bool:
    r = clean_rut(rut)
    if len(r) < 2:
        return False

    body, dv = r[:-1], r[-1]
    if not body.isdigit():
        return False

    factors = [2, 3, 4, 5, 6, 7]
    s = 0
    for i, ch in enumerate(reversed(body)):
        s += int(ch) * factors[i % len(factors)]

    mod = 11 - (s % 11)
    if mod == 11:
        expected = "0"
    elif mod == 10:
        expected = "K"
    else:
        expected = str(mod)

    return dv == expected


def validate_strong_password(pw: str) -> None:
    if len(pw) < 8:
        raise serializers.ValidationError("Password must be at least 8 characters long.")
    if not re.search(r"[A-Z]", pw):
        raise serializers.ValidationError("Password must include at least one uppercase letter.")
    if len(re.findall(r"\d", pw)) < 2:
        raise serializers.ValidationError("Password must include at least two numbers.")


class PatientRegisterSerializer(serializers.Serializer):
    rut = serializers.CharField()
    nombres = serializers.CharField()
    apellidos = serializers.CharField()
    genero = serializers.CharField()
    mail = serializers.EmailField()
    telefono = serializers.CharField()
    fecha_nacimiento = serializers.DateField()

    nombre_tutor = serializers.CharField(required=False, allow_blank=True, allow_null=True)
    telefono_tutor = serializers.CharField(required=False, allow_blank=True, allow_null=True)

    password = serializers.CharField(write_only=True)

    def validate_rut(self, value):
        if not validate_rut_dv(value):
            raise serializers.ValidationError("Invalid RUT.")
        return clean_rut(value)

    def validate_password(self, value):
        validate_strong_password(value)
        return value

    def validate_fecha_nacimiento(self, value):
        if value > date.today():
            raise serializers.ValidationError("fecha_nacimiento cannot be in the future.")
        return value

    def validate(self, attrs):
        rut = attrs["rut"]
        mail = attrs["mail"].lower()

        if Profile.objects.filter(rut=rut).exists():
            raise serializers.ValidationError({"rut": "RUT already exists."})
        if User.objects.filter(username=rut).exists():
            raise serializers.ValidationError({"rut": "RUT already exists."})
        if User.objects.filter(email__iexact=mail).exists():
            raise serializers.ValidationError({"mail": "Email already exists."})

        return attrs

    def create(self, validated_data):
        rut = validated_data["rut"]
        mail = validated_data["mail"].lower()

        user = User.objects.create_user(
            username=rut,  # login por RUT
            email=mail,
            password=validated_data["password"],
        )

        Profile.objects.create(
            user=user,
            rut=rut,
            nombres=validated_data["nombres"],
            apellidos=validated_data["apellidos"],
            genero=validated_data["genero"],
            telefono=validated_data["telefono"],
            fecha_nacimiento=validated_data["fecha_nacimiento"],
            nombre_tutor=validated_data.get("nombre_tutor") or None,
            telefono_tutor=validated_data.get("telefono_tutor") or None,
        )

        return user


class DoctorRegisterSerializer(serializers.Serializer):
    rut = serializers.CharField()
    nombres = serializers.CharField()
    apellidos = serializers.CharField()
    genero = serializers.CharField()
    mail = serializers.EmailField()
    telefono = serializers.CharField()
    fecha_nacimiento = serializers.DateField()
    password = serializers.CharField(write_only=True)

    def validate_rut(self, value):
        if not validate_rut_dv(value):
            raise serializers.ValidationError("Invalid RUT.")
        return clean_rut(value)

    def validate_password(self, value):
        validate_strong_password(value)
        return value

    def validate_mail(self, value):
        mail = value.lower().strip()
        if not mail.endswith("@rednorte.com"):
            raise serializers.ValidationError("Doctor email must end with @rednorte.com.")
        return mail

    def validate_fecha_nacimiento(self, value):
        if value > date.today():
            raise serializers.ValidationError("fecha_nacimiento cannot be in the future.")
        return value

    def validate(self, attrs):
        rut = attrs["rut"]
        mail = attrs["mail"].lower()

        if Profile.objects.filter(rut=rut).exists():
            raise serializers.ValidationError({"rut": "RUT already exists."})
        if User.objects.filter(username=rut).exists():
            raise serializers.ValidationError({"rut": "RUT already exists."})
        if User.objects.filter(email__iexact=mail).exists():
            raise serializers.ValidationError({"mail": "Email already exists."})

        return attrs

    def create(self, validated_data):
        rut = validated_data["rut"]
        mail = validated_data["mail"].lower()

        user = User.objects.create_user(
            username=rut,  # login por RUT
            email=mail,
            password=validated_data["password"],
        )

        # 1 rol: doctor
        doctor_group, _ = Group.objects.get_or_create(name="doctor")
        user.groups.clear()
        user.groups.add(doctor_group)

        Profile.objects.create(
            user=user,
            rut=rut,
            nombres=validated_data["nombres"],
            apellidos=validated_data["apellidos"],
            genero=validated_data["genero"],
            telefono=validated_data["telefono"],
            fecha_nacimiento=validated_data["fecha_nacimiento"],
        )

        return user


class ChangePasswordSerializer(serializers.Serializer):
    old_password = serializers.CharField(write_only=True)
    new_password = serializers.CharField(write_only=True)

    def validate_new_password(self, value):
        validate_strong_password(value)
        return value

    def validate(self, attrs):
        user = self.context["request"].user
        if not user.check_password(attrs["old_password"]):
            raise serializers.ValidationError({"old_password": "Old password is incorrect."})
        return attrs

    def save(self, **kwargs):
        user = self.context["request"].user
        user.set_password(self.validated_data["new_password"])
        user.save(update_fields=["password"])
        return user