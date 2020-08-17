package id.fathonyfath.quran.lite.utils.dialogManager;

import android.content.Context;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class DialogManager {
    private static Map<Integer, SimpleDialog.Factory> dialogFactoryHolder = new HashMap<>();

    public static <T extends SimpleDialog> void registerFactory(Class<T> dialogClass, SimpleDialog.Factory factory) {
        if (!DialogManager.dialogFactoryHolder.containsKey(dialogClass.hashCode())) {
            DialogManager.dialogFactoryHolder.put(dialogClass.hashCode(), factory);
        }
    }

    public static <T extends SimpleDialog> SimpleDialog createDialog(int classHashCode, Context context, Parcelable arguments) {
        SimpleDialog.Factory factory = DialogManager.dialogFactoryHolder.get(classHashCode);
        if (factory == null) {
            throw new IllegalStateException("No factory registered for class with hashCode " + classHashCode);
        }

        return factory.create(context, arguments);
    }
}

