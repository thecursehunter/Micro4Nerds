package ueh.edu.vn.md.micro4nerds.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ueh.edu.vn.md.micro4nerds.R;

import java.util.List;
public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private List<Integer> listImages; // Lưu danh sách ID ảnh (R.drawable...)

    public BannerAdapter(List<Integer> listImages) {
        this.listImages = listImages;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.imgBanner.setImageResource(listImages.get(position));
    }

    @Override
    public int getItemCount() {
        if (listImages != null) {
            return listImages.size();
        }
        return 0;
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBanner;

        public BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBanner = itemView.findViewById(R.id.imgBanner);
        }
    }
}
