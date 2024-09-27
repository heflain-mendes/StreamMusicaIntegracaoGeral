import br.com.interfaces.model.IMusica;
import br.com.interfaces.model.IUsuario;
import br.com.interfaces.services.IArtistaService;
import br.com.interfaces.services.IPlayListService;
import br.com.interfaces.services.IRecomendacaoService;
import br.com.interfaces.services.IReproducaoService;
import br.com.model.Musica;
import br.com.model.Playlist;
import br.com.model.Usuario;
import br.com.musicas.reproducao.ReproducaoService;
import br.com.repositories.ArtistaRepository;
import br.com.repositories.HistoricoReproducaoRepository;
import br.com.repositories.MusicaRepository;
import br.com.repositories.UsuarioRepository;
import br.com.services.ArtistaService;
import br.com.services.PlayListService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.ufes.gqs.recomendacaoservice.services.RecomendacaoService;

public class ReproducaoMusica {
    public static IArtistaService artistaService;
    public static IReproducaoService reproducaoService;
    public static IRecomendacaoService recomendacaoService;
    public static IPlayListService playListService;

    @BeforeAll
    public static void setUp(){
        artistaService = new ArtistaService(
            ArtistaRepository.getArtistaRepository(),
            MusicaRepository.getMusicaRepository()
        );
        reproducaoService = new ReproducaoService();
        recomendacaoService = new RecomendacaoService(
            MusicaRepository.getMusicaRepository(),
            HistoricoReproducaoRepository.getHistoricoReproducaoRepository(),
            UsuarioRepository.getUsuarioRepository()
        );
        playListService = new PlayListService(recomendacaoService);
    }

    @Test
    public void deveReproduzirMusicaExibirInformacoesArtistaEParar(){
        IMusica musica = new Musica("Sweet Child O' Mine", "Guns N' Roses", "Rock", 5.5);
        IUsuario usuario = new Usuario("Usuáio", "usuario@email", true, true);

        reproducaoService.reproduzirMusica(musica, usuario);
        reproducaoService.exibirInformacoesArtistaDuranteReproducao(musica, artistaService);
        reproducaoService.pararReproducao(usuario);
    }

    @Test
    public void deveReproduzirMusicaExibirRecomendacaoEParar(){
        IMusica musica = new Musica("Sweet Child O' Mine", "Guns N' Roses", "Rock", 5.5);
        IUsuario usuario = new Usuario("Usuáio", "usuario@email", true, true);

        reproducaoService.reproduzirMusica(musica, usuario);
        reproducaoService.obterRecomendacoesDuranteReproducao(recomendacaoService, usuario);
        reproducaoService.pararReproducao(usuario);
    }
    
    @Test
    public void deveReproduzirPlayListRecomendacaoEParar(){
        IUsuario usuario = new Usuario("Usuáio", "usuario@email", true, true);
        Playlist playList = new Playlist("Nova", usuario);
        var musicas = MusicaRepository.getMusicaRepository().findMaisTocadasByGenero("KPOP", 2);
        
        for(var mus : musicas.get()){
            playList.adicionarMusica(mus);
        }
        
        reproducaoService.reproduzirPlayList(playList, usuario, artistaService);
        reproducaoService.obterRecomendacoesDuranteReproducao(recomendacaoService, usuario);
        reproducaoService.pausarReproducao(usuario);
    }
    
    @Test
    public void deveReproduzirPlayListPararERetomar(){
        IUsuario usuario = new Usuario("Usuáio", "usuario@email", true, true);
        Playlist playList = new Playlist("Nova", usuario);
        var musicas = MusicaRepository.getMusicaRepository().findMaisTocadasByGenero("KPOP", 2);
        
        for(var mus : musicas.get()){
            playList.adicionarMusica(mus);
        }
        
        reproducaoService.reproduzirPlayList(playList, usuario, artistaService);
        reproducaoService.pausarReproducao(usuario);
        reproducaoService.retomarReproducao(usuario);
    }
    
    @Test
    public void deveReproduzirPlayListPausarBuscarBiografiaReproduzirMusica(){
        IMusica musica = new Musica("Sweet Child O' Mine", "Guns N' Roses", "Rock", 5.5);
        IUsuario usuario = new Usuario("Usuáio", "usuario@email", true, true);
        Playlist playList = new Playlist("Nova", usuario);
        var musicas = MusicaRepository.getMusicaRepository().findMaisTocadasByGenero("KPOP", 2);
        
        for(var mus : musicas.get()){
            playList.adicionarMusica(mus);
        }
        
        reproducaoService.reproduzirPlayList(playList, usuario, artistaService);
        reproducaoService.pausarReproducao(usuario);
        artistaService.getBiografia(playList.getMusicas().getFirst().getArtista());
        reproducaoService.reproduzirMusica(musica, usuario);
    }
    
    @Test
    public void deveReproduzirPlayListCompartilharEReproduzir(){
        IUsuario usuario = new Usuario("Usuáio", "usuario@email", true, true);
        IUsuario usuario2 = new Usuario("Usuáio2", "usuario2@email", true, true);
        
        Playlist playList = new Playlist("Nova", usuario);
        var musicas = MusicaRepository.getMusicaRepository().findMaisTocadasByGenero("KPOP", 2);
        
        for(var mus : musicas.get()){
            playList.adicionarMusica(mus);
        }
        
        reproducaoService.reproduzirPlayList(playList, usuario, artistaService);
        playListService.compartilharPlayList(playList, usuario2);
        reproducaoService.reproduzirPlayList(playList, usuario2, artistaService);
    }
}
