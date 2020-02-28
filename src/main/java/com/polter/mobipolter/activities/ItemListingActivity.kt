package com.polter.mobipolter.activities

import android.app.Activity
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import com.polter.mobipolter.R
import com.polter.mobipolter.activities.adapters.ItemListAdapter
import com.polter.mobipolter.activities.adapters.onItemClick
import com.polter.mobipolter.activities.model.KindEntity
import com.polter.mobipolter.activities.model.PredefinedListEntity
import com.polter.mobipolter.activities.model.SpeciesEntity
import com.polter.mobipolter.activities.utility.DividerItemDecorations
import com.polter.mobipolter.activities.utility.Utilities
import com.polter.mobipolter.activities.viewmodel.PredefinedListViewModel
import kotlinx.android.synthetic.main.activity_item_listing.*
import kotlinx.android.synthetic.main.activity_item_listing.imgBack
import kotlinx.android.synthetic.main.dialog_add_kind.view.*
import kotlinx.android.synthetic.main.dialog_add_specie.edtAbbr
import kotlinx.android.synthetic.main.dialog_add_specie.edtName
import kotlinx.android.synthetic.main.dialog_add_specie.view.*
import kotlinx.android.synthetic.main.dialog_add_specie.view.btnAdd
import kotlinx.android.synthetic.main.dialog_add_specie.view.btnCancel
import kotlinx.android.synthetic.main.dialog_add_specie.view.edtAbbr
import kotlinx.android.synthetic.main.dialog_add_specie.view.edtName


class ItemListingActivity : AppCompatActivity(), onItemClick {

    var currentItemName : String? = null
    var mAdapter : ItemListAdapter? = null
    var mSpeciesList : List<SpeciesEntity> ? = null
    var mKindList : List<KindEntity> ? = null
    var viewModel : PredefinedListViewModel ? = null
    lateinit var predefinedListEntity : ArrayList<PredefinedListEntity>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item_listing)

        currentItemName = intent.getStringExtra(Utilities.CURRENT_ITEM_NAME)
        rvItemList.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        rvItemList.addItemDecoration(DividerItemDecorations(ContextCompat.getDrawable(getApplicationContext(),
                R.drawable.item_decorator)!!))

        predefinedListEntity = ArrayList()

        viewModel = ViewModelProviders.of(this, ViewModelProviders.DefaultFactory(this.application)).get(PredefinedListViewModel::class.java!!)

        viewModel?.allSpecies?.observe(this, Observer<List<SpeciesEntity>> { speciesListDB ->

            mSpeciesList = speciesListDB!!
            predefinedListEntity = ArrayList()
            if(mSpeciesList!!.size > 0 ){
                if(currentItemName.equals(getString(R.string.species))){
                    itemTitle.text = getString(R.string.species)
                    itemName.text = getString(R.string.species)


                    // val myArrayList = resources.getStringArray(R.array.species_list)
                    if(mSpeciesList == null && mSpeciesList?.size == 0 )
                        mSpeciesList = ArrayList()

                    for(speciesEntity : SpeciesEntity in mSpeciesList!!){

                        val predefinedEntity = PredefinedListEntity(speciesEntity.speciesAbbr,
                                speciesEntity.speciesName,speciesEntity.speciesLatName)

                        predefinedListEntity.add(predefinedEntity)
                    }


                    if(mAdapter == null){

                        mAdapter = ItemListAdapter(predefinedListEntity,false,getString(R.string.species),this,this)
                        rvItemList.adapter = mAdapter
                    }else{
                        mAdapter?.itemList = predefinedListEntity
                        mAdapter!!.notifyDataSetChanged()
                        rvItemList.scrollToPosition(mSpeciesList!!.size-1)
                    }

                }

            }

        })

        viewModel?.allKind?.observe(this, Observer<List<KindEntity>> { kindListDB ->

            mKindList = kindListDB
            predefinedListEntity = ArrayList()
            if(mKindList!!.size > 0 ){
                if(currentItemName.equals(getString(R.string.kind))){
                    itemTitle.text = getString(R.string.kind)
                    itemName.text = getString(R.string.kind)

                    // val myArrayList = resources.getStringArray(R.array.species_list)
                    if(mKindList == null)
                        mKindList = ArrayList()


                    for(kindEntity : KindEntity in mKindList!!){

                        val predefinedEntity = PredefinedListEntity(kindEntity.kindAbbr,
                                kindEntity.kindName,kindEntity.kindNo,null,kindEntity.isKindSpecial)

                        predefinedListEntity.add(predefinedEntity)
                    }

                    if(mAdapter == null){
                        mAdapter = ItemListAdapter(predefinedListEntity,false,getString(R.string.kind),this,this)
                        rvItemList.adapter = mAdapter
                    }else{
                        mAdapter?.itemList = predefinedListEntity
                        mAdapter!!.notifyDataSetChanged()
                        rvItemList.scrollToPosition(mKindList!!.size-1)
                    }

                }

            }

        })

/*
        if(currentItemName.equals(getString(R.string.kind))){
            itemTitle.text = getString(R.string.kind)
            itemName.text = getString(R.string.kind)
            val myArrayList = resources.getStringArray(R.array.kind_list)

            for(name in myArrayList){

                val predefinedEntity = PredefinedListEntity(name,"","")
                predefinedListEntity.add(predefinedEntity)
            }

            mAdapter = ItemListAdapter(predefinedListEntity,false,getString(R.string.kind),this,this)
            rvItemList.adapter = mAdapter
        }else */

            if(currentItemName.equals(getString(R.string.quality))){
            itemTitle.text = getString(R.string.quality)
            itemName.text = getString(R.string.quality)
            val myArrayList = resources.getStringArray(R.array.quality_list)
            for(name in myArrayList){

                val predefinedEntity = PredefinedListEntity(name,"","")
                predefinedListEntity.add(predefinedEntity)
            }
            mAdapter = ItemListAdapter(predefinedListEntity,false,getString(R.string.quality),this,this)
            rvItemList.adapter = mAdapter
        }else if(currentItemName.equals(getString(R.string.size))){
            itemTitle.text = getString(R.string.size)
            itemName.text = getString(R.string.size)
            val myArrayList = resources.getStringArray(R.array.size_list)
            for(name in myArrayList){

                val predefinedEntity = PredefinedListEntity(name,"","")
                predefinedListEntity.add(predefinedEntity)
            }
            mAdapter = ItemListAdapter(predefinedListEntity,false,getString(R.string.size),this,this)
            rvItemList.adapter = mAdapter
        }

        imgBack.setOnClickListener {

            var intent = Intent()
            setResult(Activity.RESULT_CANCELED,intent)
            finish()
        }

        imgAdd.setOnClickListener {

            if(itemTitle.text.toString().equals(getString(R.string.species))){
                showDialog("")
            }else if(itemTitle.text.toString().equals(getString(R.string.kind))){
                showKindDialog("")
            }else{

            }

        }

    }


    override fun itemClick(entity: PredefinedListEntity) {

        var intent = Intent()
        intent.putExtra(Utilities.CURRENT_ITEM_NAME,currentItemName)
        if(currentItemName.equals(getString(R.string.species))){
            intent.putExtra(Utilities.CURRENT_ITEM_VALUE,entity.itemAbbr)
        }else if(currentItemName.equals(getString(R.string.kind))){
            intent.putExtra(Utilities.CURRENT_ITEM_VALUE,entity.itemLatName)
            intent.putExtra(Utilities.CURRENT_KIND_ENTITY,entity)
        }else{
            intent.putExtra(Utilities.CURRENT_ITEM_VALUE,entity.itemAbbr)
        }

        setResult(Activity.RESULT_OK,intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        var intent = Intent()
        setResult(Activity.RESULT_CANCELED,intent)
        finish()
    }

    private fun showDialog(title: String) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyAlertDialogTheme)

        val inflater = this.layoutInflater
        var dialogView = inflater.inflate(R.layout.dialog_add_specie, null)

        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.btnAdd.setOnClickListener {


            if(TextUtils.isEmpty(dialogView.edtAbbr.text.toString())){
                edtAbbr.setError("Field Required")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(dialogView.edtName.text.toString())){
                edtName.setError("Field Required")
                return@setOnClickListener
            }

            var latname = ""
            if(TextUtils.isEmpty(dialogView.edtLatName.text.toString())){
                latname = dialogView.edtLatName.text.toString()
            }

            val speciesEntity = SpeciesEntity(dialogView.edtAbbr.text.toString(),dialogView.edtName.text.toString(),
                    dialogView.edtLatName.text.toString())

            viewModel?.insertSpecies(speciesEntity)
            alertDialog.dismiss()

        }

        dialogView.btnCancel.setOnClickListener {

            alertDialog.dismiss()
        }

    }

    private fun showKindDialog(title: String) {
        val dialogBuilder = AlertDialog.Builder(this, R.style.MyAlertDialogTheme)

        val inflater = this.layoutInflater
        var dialogView = inflater.inflate(R.layout.dialog_add_kind, null)

        dialogBuilder.setView(dialogView)
        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        dialogView.btnAdd.setOnClickListener {


            if(TextUtils.isEmpty(dialogView.edtAbbr.text.toString())){
                edtAbbr.setError("Field Required")
                return@setOnClickListener
            }
            if(TextUtils.isEmpty(dialogView.edtName.text.toString())){
                edtName.setError("Field Required")
                return@setOnClickListener
            }

            var latname = ""
            if(TextUtils.isEmpty(dialogView.edtKindNo.text.toString())){
                latname = dialogView.edtKindNo.text.toString()
            }

            var isChecked = false
            if(dialogView.chkKindSpecial.isChecked){
                isChecked = true
            }

            val kindEntity = KindEntity(dialogView.edtAbbr.text.toString(),dialogView.edtName.text.toString(),
                    dialogView.edtKindNo.text.toString(),isChecked)

            viewModel?.insertKind(kindEntity)
            alertDialog.dismiss()

        }

        dialogView.btnCancel.setOnClickListener {

            alertDialog.dismiss()
        }

    }

}