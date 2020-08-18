package id.fathonyfath.quran.lite.utils.dialogManager;

import android.content.Context;
import android.os.Parcelable;

import java.util.HashMap;
import java.util.Map;

public class DialogManager {
    private static Map<Integer, Dialog.Factory> dialogFactoryHolder = new HashMap<>();

    public static <T extends Dialog> void registerFactory(Class<T> dialogClass, Dialog.Factory factory) {
        if (!DialogManager.dialogFactoryHolder.containsKey(dialogClass.hashCode())) {
            DialogManager.dialogFactoryHolder.put(dialogClass.hashCode(), factory);
        }
    }

    public static <T extends Dialog> Dialog createDialog(int classHashCode, Context context, Parcelable arguments) {
        Dialog.Factory factory = DialogManager.dialogFactoryHolder.get(classHashCode);
        if (factory == null) {
            throw new IllegalStateException("No factory registered for class with hashCode " + classHashCode);
        }

        return factory.create(context, arguments);
    }
}

