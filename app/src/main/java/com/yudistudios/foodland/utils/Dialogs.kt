package com.yudistudios.foodland.utils

import android.app.AlertDialog
import android.content.Context

import android.view.LayoutInflater
import android.view.View
import com.yudistudios.foodland.databinding.DialogErrorBinding
import com.yudistudios.foodland.databinding.DialogLoadingBinding
import com.yudistudios.foodland.databinding.DialogSuccessBinding


class Dialogs {

    fun loadingDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = DialogLoadingBinding.inflate(inflater, null, false)
        val dialogView: View = binding.root
        builder.setView(dialogView)

        return builder.create()
    }

    fun errorDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = DialogErrorBinding.inflate(inflater, null, false)
        val dialogView: View = binding.root
        builder.setView(dialogView)

        val dialog = builder.create()

        binding.buttonOk.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun successDialog(context: Context): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = DialogSuccessBinding.inflate(inflater, null, false)
        val dialogView: View = binding.root
        builder.setView(dialogView)

        val dialog = builder.create()

        binding.buttonOk.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    fun successDialog(context: Context, okOnClick: () -> Unit): AlertDialog {
        val builder = AlertDialog.Builder(context)
        val inflater = LayoutInflater.from(context)
        val binding = DialogSuccessBinding.inflate(inflater, null, false)
        val dialogView: View = binding.root
        builder.setView(dialogView)
        builder.setCancelable(false)

        val dialog = builder.create()

        binding.buttonOk.setOnClickListener {
           okOnClick()
        }

        return dialog
    }
}