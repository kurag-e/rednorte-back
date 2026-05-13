from django.contrib import admin
from django.contrib.auth.admin import UserAdmin as DjangoUserAdmin
from django.contrib.auth.models import User, Group

from .models import Profile


ROLE_GROUPS = {"patient", "doctor", "admin"}


class ProfileInline(admin.StackedInline):
    model = Profile
    can_delete = False
    extra = 0


class UserAdmin(DjangoUserAdmin):
    inlines = [ProfileInline]

    def save_model(self, request, obj, form, change):
        """
        Enforce:
        - 1 solo rol (patient/doctor/admin) por usuario (usando Groups)
        - si rol == admin => is_staff True, si no => False
        """
        super().save_model(request, obj, form, change)

        role_groups = list(obj.groups.filter(name__in=ROLE_GROUPS))
        if len(role_groups) > 1:
            # dejamos solo el primero (por orden alfabético para consistencia)
            role_groups.sort(key=lambda g: g.name)
            keep = role_groups[0]
            obj.groups.set([keep])

        is_admin = obj.groups.filter(name="admin").exists()

        # A1: admin => staff (acceso a /admin/)
        if obj.is_staff != is_admin:
            obj.is_staff = is_admin
            obj.save(update_fields=["is_staff"])


# Re-registrar User con nuestro admin personalizado
admin.site.unregister(User)
admin.site.register(User, UserAdmin)

# Registrar Profile para verlo también como tabla independiente (opcional)
@admin.register(Profile)
class ProfileAdmin(admin.ModelAdmin):
    list_display = ("rut", "nombres", "apellidos", "telefono", "especialidad", "created_at")
    search_fields = ("rut", "nombres", "apellidos", "telefono", "user__email")
# Register your models here.
