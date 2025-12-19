package vn.hcmute.bt11.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import vn.hcmute.bt11.databinding.ItemTaskBinding
import vn.hcmute.bt11.models.Task

class TaskAdapter(
    private var tasks: MutableList<Task>,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit,
    private val onCheckChange: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(private val binding: ItemTaskBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(task: Task) {
            binding.apply {
                tvTitle.text = task.title
                tvDescription.text = task.description ?: "Không có mô tả"
                tvDate.text = task.date ?: "Chưa có ngày"
                tvTime.text = task.time ?: "Chưa có giờ"

                // Remove listener before setting checked state to avoid triggering during bind
                cbCompleted.setOnCheckedChangeListener(null)
                cbCompleted.isChecked = task.isCompleted

                // Strike through completed tasks
                if (task.isCompleted) {
                    tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                }

                // Hide description if empty
                if (task.description.isNullOrEmpty()) {
                    tvDescription.visibility = View.GONE
                } else {
                    tvDescription.visibility = View.VISIBLE
                }

                // Set listener after setting the initial state
                cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                    task.isCompleted = isChecked
                    onCheckChange(task)
                    // Update the strike-through immediately without notifying adapter
                    if (isChecked) {
                        tvTitle.paintFlags = tvTitle.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                    } else {
                        tvTitle.paintFlags = tvTitle.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                    }
                }

                btnEdit.setOnClickListener {
                    onEditClick(task)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(task)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TaskViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks.clear()
        tasks.addAll(newTasks)
        notifyDataSetChanged()
    }

    fun removeTask(position: Int) {
        tasks.removeAt(position)
        notifyItemRemoved(position)
    }
}

