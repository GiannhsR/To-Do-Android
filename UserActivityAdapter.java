package giannhsrak.activitiestracker;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;

/**
 * Created by Giannhs on 23/5/2017.
 * Η κλάσση αυτή χρησιμοποιείται για τη δυναμική δημιουργία Views
 * κατά την διάρκεια που η εφαρμογή τρέχει(runtime)
 * Συγκεκριμένα εμφανίζει ένα TextView για κάθε activity που προϋπάρχει στο Database
 * Το κάθε TextView περιέχει κάποιο activity
 */

public class UserActivityAdapter extends ArrayAdapter<String> {

    public UserActivityAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if( convertView == null ){
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_layout,parent,false);
        }

        TextView activityLabel = (TextView) convertView.findViewById(R.id.activityLabel);

        String usersActivityName = getItem(position);

        activityLabel.setText(usersActivityName);

        return convertView;
    }

}
