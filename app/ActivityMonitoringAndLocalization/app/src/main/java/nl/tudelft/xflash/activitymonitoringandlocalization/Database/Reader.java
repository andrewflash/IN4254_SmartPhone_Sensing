package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.Context;
import android.os.Environment;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by xflash on 4-5-16.
 */
public class Reader extends AbstractReader{

    public Reader(Context ctx, String newFileName) {
        super(ctx);
        this.fileName = newFileName;
        try {
            fInpStream = new DataInputStream(new FileInputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}