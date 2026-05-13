from django.conf import settings
from django.db import models


class Profile(models.Model):
    user = models.OneToOneField(
        settings.AUTH_USER_MODEL,
        on_delete=models.CASCADE,
        related_name="profile",
    )

    rut = models.CharField(max_length=12, unique=True)  # 12345678K
    nombres = models.CharField(max_length=100)
    apellidos = models.CharField(max_length=100)
    genero = models.CharField(max_length=20)

    telefono = models.CharField(max_length=20)
    fecha_nacimiento = models.DateField()

    nombre_tutor = models.CharField(max_length=120, blank=True, null=True)
    telefono_tutor = models.CharField(max_length=20, blank=True, null=True)

    especialidad = models.CharField(max_length=80, blank=True, null=True)

    created_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.rut} - {self.nombres} {self.apellidos}"
# Create your models here.
