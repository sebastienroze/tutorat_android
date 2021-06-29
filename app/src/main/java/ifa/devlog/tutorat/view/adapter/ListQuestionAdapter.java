package ifa.devlog.tutorat.view.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ifa.devlog.tutorat.R;
import ifa.devlog.tutorat.model.Question;

public class ListQuestionAdapter extends RecyclerView.Adapter
        <RecyclerView.ViewHolder> {

    private EcouteurClicQuestion ecouteur;


    public interface EcouteurClicQuestion {
        void onClicQuestion(Question item);
    }

    private List<Question> listeQuestion;

    public ListQuestionAdapter(List<Question> listeQuestion, EcouteurClicQuestion ecouteur) {
        this.ecouteur = ecouteur;
        this.listeQuestion = listeQuestion;
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {

        LinearLayout layoutItem;
        TextView textViewSujet;
        TextView textViewExplication;
        TextView textViewDate;
        CardView cardView;
        TextView textViewRepondu;


        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item_question);
            textViewSujet = itemView.findViewById(R.id.textView_sujet);
            textViewExplication = itemView.findViewById(R.id.textView_explication);
            textViewDate = itemView.findViewById(R.id.textView_date);
            textViewRepondu  = itemView.findViewById(R.id.textView_repondu);
            cardView = itemView.findViewById(R.id.cardView_question);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_question,parent,false);
        return new QuestionViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = listeQuestion.get(position);
        QuestionViewHolder questionViewHolder = (QuestionViewHolder) holder;
        questionViewHolder.textViewSujet.setText(question.getSujet());
        questionViewHolder.textViewExplication.setText(question.getExplication());
        questionViewHolder.textViewDate.setText(question.getTexteDate_question());
        if (!question.estRepondue()) questionViewHolder.textViewRepondu.setVisibility(View.GONE);
        questionViewHolder.layoutItem.setOnClickListener( v ->
                ecouteur.onClicQuestion(question)
        );

        ViewGroup.MarginLayoutParams  layoutParams;
        if(position == 0 ){
            layoutParams = (ViewGroup.MarginLayoutParams)questionViewHolder.layoutItem.getLayoutParams();
            layoutParams.setMargins(0,128,0,0);
            questionViewHolder.layoutItem.setLayoutParams(layoutParams);
        } else if (position == listeQuestion.size() - 1) {
            layoutParams = (ViewGroup.MarginLayoutParams)questionViewHolder.layoutItem.getLayoutParams();
            layoutParams.setMargins(0,0,0,96);
            questionViewHolder.layoutItem.setLayoutParams(layoutParams);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        return listeQuestion.size();
    }
}
