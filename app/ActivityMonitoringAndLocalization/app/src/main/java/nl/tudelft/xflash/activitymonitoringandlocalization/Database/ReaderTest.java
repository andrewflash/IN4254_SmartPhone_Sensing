package nl.tudelft.xflash.activitymonitoringandlocalization.Database;

import android.content.Context;

import java.io.DataInputStream;

/**
 * Created by xflash on 4-5-16.
 */
public class ReaderTest extends AbstractReader {
    public ReaderTest(Context ctx, int resourceId){
        super(ctx);
        fInpStream = new DataInputStream(ctx.getResources().openRawResource(resourceId));
    }
}
