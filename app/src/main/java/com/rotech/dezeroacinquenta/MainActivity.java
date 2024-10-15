package com.rotech.dezeroacinquenta;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Random;

public class MainActivity extends Activity implements View.OnClickListener{

    private static boolean isPlaying = false, isWaitingDose = false, isInModoSelecionar;
    private int numeroCorreto = 0;

    //Define as ID's
    private static final int[] BUTTON_IDS = {
            R.id.B01, R.id.B02, R.id.B03, R.id.B04, R.id.B05,
            R.id.B06, R.id.B07, R.id.B08, R.id.B09, R.id.B10,
            R.id.B11, R.id.B12, R.id.B13, R.id.B14, R.id.B15,
            R.id.B16, R.id.B17, R.id.B18, R.id.B19, R.id.B20,
            R.id.B21, R.id.B22, R.id.B23, R.id.B24, R.id.B25,
            R.id.B26, R.id.B27, R.id.B28, R.id.B29, R.id.B30,
            R.id.B31, R.id.B32, R.id.B33, R.id.B34, R.id.B35,
            R.id.B36, R.id.B37, R.id.B38, R.id.B39, R.id.B40,
            R.id.B41, R.id.B42, R.id.B43, R.id.B44, R.id.B45,
            R.id.B46, R.id.B47, R.id.B48, R.id.B49, R.id.B50,
    };

    //Define o Array de Botões
    Button[] button = new Button[51];
    Button bSortear = null, bSelecionar = null;

    private InterstitialAd mInterstitialAd;
    private AdRequest adRequestIntersticial;

    public void popupIntrucoes(){
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        PopupWindow pw = new PopupWindow(inflater.inflate(R.layout.popup_instrucoes, null, false),dm.widthPixels-150, dm.heightPixels-150, true);
        pw.showAtLocation(this.findViewById(R.id.activity_main), Gravity.CENTER, 0, 0);
    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.actions, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_removeAds) {//Toast.makeText(getApplicationContext(), "clicou removeAds", Toast.LENGTH_SHORT).show();
                    String url = getString(R.string.urlZeroACinquentaPremium);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                } else if (itemId == R.id.menu_comoJogar) {//Toast.makeText(getApplicationContext(), "Como Jogar", Toast.LENGTH_SHORT).show();
                    popupIntrucoes();
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instancia itens fundamentais
        bSortear = (Button)findViewById(R.id.bSortear);
        bSelecionar = (Button)findViewById(R.id.bSelecionar);
        final Random gerador = new Random();

        //Instanciador de botões
        int j=1;
        for(int id:BUTTON_IDS){
            button[j] = (Button)findViewById(id);
            button[j].setOnClickListener(this);
            j++;
        }

        //Starts Ads
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        //Anúncio Banner
        AdView adView = (AdView)findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        //Anúncio Intersticial
        adRequestIntersticial = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.adIntersticialID), adRequestIntersticial,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                        Log.i("TAG", "onAdLoaded");
                        //Toast.makeText(getApplicationContext(), "onAdLoaded", Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        Log.i("TAG", loadAdError.getMessage());
                        mInterstitialAd = null;
                        //Toast.makeText(getApplicationContext(), "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                    }
                });

        bSortear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.dialogConfirmarTitulo)
                            .setMessage(R.string.dialogConfirmarMensagem)
                            .setPositiveButton(R.string.dialogSim, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    isPlaying = false;
                                    bSortear.performClick();
                                }
                            })
                            .setNegativeButton(R.string.dialogNao, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setCancelable(true)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                AdView adView = (AdView)findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                isInModoSelecionar = false;
                int gerado = gerador.nextInt(50) + 1;
                //Debug mode: Toast.makeText(getApplicationContext(), String.valueOf(gerado) + " foi o valor escolhido", Toast.LENGTH_SHORT).show();
                Toast.makeText(getApplicationContext(), R.string.txtNumeroSorteado, Toast.LENGTH_SHORT).show();
                MediaPlayer dEscolhida = MediaPlayer.create(getApplicationContext(), R.raw.descolhida);
                dEscolhida.start();
                startGame(gerado);
            }
        });

        bSelecionar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPlaying){
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle(R.string.dialogConfirmarTitulo)
                            .setMessage(R.string.dialogConfirmarMensagem)
                            .setPositiveButton(R.string.dialogSim, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    isPlaying = false;
                                    bSelecionar.performClick();
                                }
                            })
                            .setNegativeButton(R.string.dialogNao, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    return;
                                }
                            })
                            .setCancelable(true)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                    return;
                }
                AdView adView = (AdView)findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder().build();
                adView.loadAd(adRequest);
                isInModoSelecionar = true;
                isWaitingDose = true;
                bSelecionar.setEnabled(false);
                bSortear.setEnabled(false);
                habilitarBotoes(true);
                Toast.makeText(getApplicationContext(), R.string.txtEscondaADose, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public int adapterViewToID(View v){
        int i;
        for (i=1; i<=50; i++){
            if(v == button[i]){
                break;
            }
        }
        return i;
    }

    @Override
    public void onClick(View v) {
        if(isWaitingDose){
            esconderDose(adapterViewToID(v));
            return;
        }

        if(v == button[numeroCorreto]) {
            MediaPlayer bCorreta = MediaPlayer.create(getApplicationContext(), R.raw.correto);
            bCorreta.start();
            escolhaCerta();
        }

        excluirImpossiveis(adapterViewToID(v));
    }

    public void excluirImpossiveis(int bClicado) {
        MediaPlayer bClick = MediaPlayer.create(getApplicationContext(), R.raw.click);
        bClick.start();
        if(bClicado<numeroCorreto)
            for (int i = 1; i <= bClicado; i++)
                button[i].setEnabled(false);

        if(bClicado>numeroCorreto)
            for(int i=bClicado; i<=50; i++)
                button[i].setEnabled(false);
    }

    private void esconderDose(int valorEscolhido){
        bSelecionar.setEnabled(true);
        bSortear.setEnabled(true);
        isWaitingDose = false;
        //Debug mode: Toast.makeText(getApplicationContext(), String.valueOf(valorEscolhido) + " foi escolhido!", Toast.LENGTH_SHORT).show();
        Toast.makeText(getApplicationContext(), R.string.txtDoseEscondida, Toast.LENGTH_SHORT).show();
        MediaPlayer dEscolhida = MediaPlayer.create(getApplicationContext(), R.raw.descolhida);
        dEscolhida.start();
        startGame(valorEscolhido);
    }

    public void habilitarBotoes(boolean ativar){
        for (int i = 1; i<=50; i++)
            button[i].setEnabled(ativar);
    }

    private void startGame(int numero){
        isPlaying = true;
        habilitarBotoes(true);
        this.numeroCorreto = numero;
    }

    private void escolhaCerta(){
        new AlertDialog.Builder(bSortear.getContext())
                .setTitle(R.string.dialogAcertoTitulo)
                .setMessage(R.string.dialogAcertoMensagem)
                .setPositiveButton(R.string.dialogAcertoContinuar, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(isInModoSelecionar) {
                            bSelecionar.performClick();
                        }else{
                            bSortear.performClick();
                        }
                    }
                })
                //.setIcon(android.R.drawable.ic_dialog_alert)
                .show();

        //Debug mode: Toast.makeText(getApplicationContext(), "Botão correto clicado", Toast.LENGTH_SHORT).show();
        habilitarBotoes(false);
        button[numeroCorreto].setEnabled(true);
        isPlaying = false;

        //Exibe anúncio depois de acertar o número
        if (mInterstitialAd != null)
            Toast.makeText(getApplicationContext(), "Carregando anúncio...pra remover anúncios clique em Configurações", Toast.LENGTH_LONG).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                    //Toast.makeText(getApplicationContext(), "ad null", Toast.LENGTH_SHORT).show();
                }
            }
        }, 5000);

        adRequestIntersticial = new AdRequest.Builder().build();
        InterstitialAd.load(this,getString(R.string.adIntersticialID), adRequestIntersticial,
            new InterstitialAdLoadCallback() {
                @Override
                public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                    mInterstitialAd = interstitialAd;
                    Log.i("TAG", "onAdLoaded");
                    //Toast.makeText(getApplicationContext(), "onAdLoaded", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    Log.i("TAG", loadAdError.getMessage());
                    mInterstitialAd = null;
                    //Toast.makeText(getApplicationContext(), "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
                }
            });
    }
}