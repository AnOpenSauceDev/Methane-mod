package me.wolfie.methane.backportutils;

import net.minecraft.util.math.Quaternion;
import org.joml.Quaternionf;
import org.joml.Vector3f;

/**
 * <li>stolen from 1.19.3's Yarn Mappings.</li>
 */
@FunctionalInterface
public interface RotationAxis  {
    public static final RotationAxis NEGATIVE_X = rad -> new Quaternionf().rotationX(-rad);
    public static final RotationAxis POSITIVE_X = rad -> new Quaternionf().rotationX(rad);
    public static final RotationAxis NEGATIVE_Y = rad -> new Quaternionf().rotationY(-rad);
    public static final RotationAxis POSITIVE_Y = rad -> new Quaternionf().rotationY(rad);
    public static final RotationAxis NEGATIVE_Z = rad -> new Quaternionf().rotationZ(-rad);
    public static final RotationAxis POSITIVE_Z = rad -> new Quaternionf().rotationZ(rad);

    public static RotationAxis of(Vector3f axis) {
        return rad -> new Quaternionf().rotationAxis(rad, axis);
    }

    public Quaternionf rotation(float var1);

    default public Quaternionf rotationDegrees(float deg) {
        return this.rotation(deg * ((float)Math.PI / 180));
    }

    public static Quaternion toOldMath(Quaternionf quaternionf){
        return new Quaternion(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w);
    }
}
