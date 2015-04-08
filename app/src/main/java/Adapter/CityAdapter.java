package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mohamedibrahim.weatherapp.R;
import com.survivingwithandroid.weather.lib.model.City;

import java.util.List;

/**
 * Created by Mohamed Ibrahim on 07/04/2015.
 */
public class CityAdapter extends BaseCustomAdapter<City> {

    private List<City> cityList;
    private Context ctx;
    LayoutInflater inflater;

    public CityAdapter(Context context, List<City> list) {
        super(context, list);
        this.cityList = list;
        this.ctx = context;
        inflater = LayoutInflater.from(ctx);
    }

    @Override
    public View onBindView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        VH holder = null;
        if (v == null) {
            v = inflater.inflate(R.layout.city_row, null, false);
            holder = new VH(v);
            v.setTag(holder);

        } else {
            holder = (VH) v.getTag();
        }
        holder.tv.setText(cityList.get(position).getName() + "," + cityList.get(position).getCountry());

        return v;
    }

    private static class VH {

        private TextView tv;

        public VH(View v) {
            tv = (TextView) v.findViewById(R.id.descrCity);
        }

    }
}
