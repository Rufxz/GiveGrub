package com.example.GiveGrub;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.ColorSpace;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class myadapter extends RecyclerView.Adapter<myadapter.myviewholder>
{
//    ArrayList<model> datalist;
    ArrayList<DocumentSnapshot> userDocList;
    FirebaseAuth fAuth= FirebaseAuth.getInstance();
    public String userID = fAuth.getCurrentUser().getUid();
    public String uid;
    FirebaseFirestore fStore;
    private Context context;


    public myadapter(ArrayList<DocumentSnapshot> list, Context context) {
        this.userDocList = list;
        this.context=context;
        fStore= FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public myviewholder onCreateViewHolder (@NonNull ViewGroup parent,int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerow, parent, false);
        return new myviewholder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull myviewholder holder, int position) {

        model mObj=userDocList.get(position).toObject(model.class);
        holder.tname.setText(mObj.getName());
        holder.ttype.setText(mObj.getType());
        holder.tdescription.setText(mObj.getDescription());

        holder.delete.setOnClickListener(v ->{
            deleteUserData(holder.getAdapterPosition(), v);
//            getSnapshots().getSnapshot(position).getReference().delete();
        } );
    }

    @Override
    public int getItemCount() {
        return userDocList.size();
    }

    class myviewholder extends RecyclerView.ViewHolder
    {
        TextView tname,ttype,tdescription;
        Button delete;
        public myviewholder(@NonNull View itemView) {
            super(itemView);
            tname = itemView.findViewById(R.id.name);
            ttype = itemView.findViewById(R.id.type);
            tdescription = itemView.findViewById(R.id.description);
            delete = itemView.findViewById(R.id.delete);
        }
    }
    private void deleteUserData(int index, View view)
    {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(view.getRootView().getContext())
                .setIcon(android.R.drawable.ic_delete)
                .setTitle("Alert")
                .setMessage("Do you want to delete Book record")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        fStore.collection("user data").document(userDocList.get(index).getId()).delete()
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful()){
                                            Toast.makeText(context,"Record deleted successfully",Toast.LENGTH_LONG).show();
                                            userDocList.remove(index);
                                            notifyDataSetChanged();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(context,"Failed to delete due to:\n"+e.getMessage(),Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                });
        alertDialog.show();
    }
}
