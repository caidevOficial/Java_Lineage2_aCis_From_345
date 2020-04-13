/*
package quests.Q1003_MataMata;

import java.util.Collection;
import java.util.HashMap;

import net.sf.l2j.gameserver.ai.CtrlIntention;
import net.sf.l2j.gameserver.ai.L2CharacterAI;
import net.sf.l2j.gameserver.datatables.NpcTable;
import net.sf.l2j.gameserver.model.L2Spawn;
import net.sf.l2j.gameserver.model.actor.L2Character;
import net.sf.l2j.gameserver.model.actor.L2Npc;
import net.sf.l2j.gameserver.model.actor.instance.L2NpcInstance;
import net.sf.l2j.gameserver.model.actor.instance.L2PcInstance;
import net.sf.l2j.gameserver.model.actor.template.NpcTemplate;
import net.sf.l2j.gameserver.model.quest.Quest;
import net.sf.l2j.gameserver.model.quest.QuestState;
//import net.sf.l2j.gameserver.model.quest.State;

/**
 * @author Kimeraweb <br>
 * @nota: Heredamos "Quest" con extends.<br>
 *        Esto significa que todo lo que hay en Quest.java es como si ya estuviera aqui escrito
 */
/*
public class Q1003_MataMata extends Quest
{
    // Es de buena costumbre anteceder de un guion los nombres de las variables de la clase.
    // Se llaman de la clase porque se declaran fueran de los metodos y desde los metodos se pueden acceder a ellas.
    // Si son constantes, se usa el nombre completo en mayusculas, ejemplo: float PI = 3.141689;
    private final int _mobId = 18342, _npcId = 1000009, _contadorTemplateID = 13169; // El mob es el gremlin de Lv1, el contador es un guardia humano
    private int _conteo; // Lleva la cuenta de los mobs muertos
    
    private final HashMap<Integer, L2NpcInstance> _listaDePjsConContadores = new HashMap<>(); // El primer parametro guarda al PJ, el segundo: el mobContador
    
    // Un HashMap es la manera mas rapida de encontrar un valor en una coleccion.
    // Funciona con pares <clave,valor>
    // Su metodo put inserta la clave y el valor.
    // Su metodo get devuelve el valor asociado a la clave.
    // Como vamos a usar put <PJ,Mob>, cuando usemos get<PJ> obtenemos el Mob del PJ.
    
    /**
     * @param questId
     * @param name
     * @param descr
     */
/*
    public Q1003_MataMata(int questId, String name, String descr)
    {
        // "super" sirve para enviar a la superclase de la que se hereda (Quest) el valor de las variables
        super(questId, name, descr);
        
        // NPC que comienza la Quest.
        // Este metodo está sobrecargado. Significa que hay otro metodo con el mismo nombre pero con parametros distintos.
        // En este caso, se acepta un numero indefinido de parametros de tipo int, ejemplo addStartNpc(1000009,10100,10101,10200);
        // Otro parametro que acepta es el objeto Collection que contenga objetos Integer: Collection<Integer> pjs = MiInstancia.getCollection();
        // Mas info en http://kimeraweb.com.es/SCJP/tema42.php
        addStartNpc(_npcId);
        
        // Muestra el primer dialogo del NPC cuando son clicados. Es decir, sustituyen el dialogo por defecto si el PJ esta haciendo una Quest donde interviene este NPC
        // addFirstTalkId() ;
        
        // Añade el NPC a esta Quest para poder leer sus links con los eventos
        // Esta sobrecargado. Tambien acepta un numero indefinido de parametros de tipo int y un objeto Collection que contenga objetos Integer.
        addTalkId(_npcId);
        
        // Añade este NPC un escuchador, para saber cuando este NPC ha muerto
        // Esta sobrecargado. Acepta numero indeterminado de parametros de tipo int.
        // Acepta un objeto Collection que contengan objetos Integer.
        addKillId(_mobId);
        
        // Añade este NPC un escuchador, para saber cuando esta siendo atacado
        // Acepta numero de parametros indeterminados int, y un Collection de objetos Integer
        // addAttackId(int npcId);
        
        // Añade al NPC la ventana de aprender skilles
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addAcquireSkillId(int npcId);
        
        // Cuando este mob es spawneado, este NPC lo sabe
        // Soporta un numero indeterminado de parametros de tipo int y una coleccion que contega objetos Integer
        // addSpawnId(int npcId);
        
        // El NPC ve a un objetivo cualquiera ejecutando una skill
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addSkillSeeId(int npcId)
        
        // Acciones que emprendera el NPC cuando se termine un casteo
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addSpellFinishedId(int... npcIds)
        
        // Al salir de una zona...
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addTrapActionId(int npcId)
        
        // Se ha visto castear una skill (sin importar quien lo haya hecho)
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addFactionCallId(int npcId)
        
        // Alguien entro en el rango agro del npc
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addAggroRangeEnterId(int npcId)
        
        // Este NPC, al ver un mob...
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addSeeCreatureId(int npcId)
        
        // Al entrar en la zona...
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addEnterZoneId(int zoneId)
        
        // Al salir de una zona
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addExitZoneId(int zoneId)
        
        // Este NPC recibe un evento de otro NPC
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addEventReceivedId(int... npcIds)
        
        // Cuando el NPC deja de moverse
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addMoveFinishedId(int... npcIds)
        
        // Cuando un NPC Walker llega al siguiente nodo
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addNodeArrivedId(int... npcIds)
        
        // Cuando un NPC Walker termina su ruta
        // Soporta un numero indeterminado de parametros de tipo int y una Coleccion que contenga objetos Integer.
        // addRouteFinishedId(int... npcIds)
    }
    
    /**
     * A continuacion los metodos invocados al usar los add. Puedes encontrar la lista de ellos en la clase Quest a partir de la linea 1144
     */
    
    /**
     * This function is called whenever a player clicks to the "Quest" link of an NPC that is registered for the quest.
     * @param npc this parameter contains a reference to the exact instance of the NPC that the player is talking with.
     * @param talker this parameter contains a reference to the exact instance of the player who is talking to the NPC.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
/*
    @Override
    public String onTalk(L2Npc npc, L2PcInstance player)
    {
        String htmltext = "<html><head><title>Personal Trainer</title></head><body>";
        QuestState st = player.getQuestState(Q1003_MataMata.class.getSimpleName());
        
        // Si la quest no esta asociada al PJ, entonces volvemos.
        if (st == null)
        {
            return getNoQuestMsg(player);
        }
        
        if (st.getState() != (STATE_STARTED))
        {
            valoresQuest(st, player, 0);
        }
        
        // Si el PJ ya estaba jugando antes (State.STARTED), cargamos los avances hechos
        if (st.getState() == (STATE_STARTED))
        {
            valoresQuest(st, player, 1);
        }
        
        htmltext = htmltext + "No dejes de hacerlo cada dia y la recompensa sera mayor!";
        return htmltext + "</body></html>";
    }
    
    /**
     * This function is called whenever a player kills a NPC that is registered for the quest.
     * @param npc this parameter contains a reference to the exact instance of the NPC that got killed.
     * @param killer this parameter contains a reference to the exact instance of the player who killed the NPC.
     * @param isSummon this parameter if it's {@code false} it denotes that the attacker was indeed the player, else it specifies that the killer was the player's pet.
     * @return the text returned by the event (may be {@code null}, a filename or just text)
     */
/*
    @Override
    public String onKill(L2Npc npc, L2PcInstance killer, boolean isSummon)
    {
        QuestState st = killer.getQuestState(Q1003_MataMata.class.getSimpleName());
        
        // Si la quest no esta asociada al PJ, entonces volvemos.
        if (st == null)
        {
            return null;
        }
        
        // Si el PJ ya ha empezado la quest y no habla con el NPC, empieza de cero. Hay que cargar los valores.
        if (st.getState() == (STATE_STARTED))
        {
            valoresQuest(st, killer, 1);
        }
        playSound(killer, QuestSound.ITEMSOUND_QUEST_ITEMGET);
        _conteo++;
        killer.sendMessage(killer.getName() + ":" + _conteo);
        
        // Grabo la hora en que mato el ultimo mob
        st.saveGlobalQuestVar("Q1003_Uvez", String.valueOf(System.currentTimeMillis()));
        // Grabo la cantidad de mobs que lleva muertos
        st.saveGlobalQuestVar("Q1003_MobsMuertos", String.valueOf(_conteo));
        
        if (st.getState() == State.CREATED)
        {
            st.setState(State.STARTED); // State.STARTED indicara que el PJ ya ha estado matando mobs
        }
        
        // Le damos una adena por cada 100 mobs que mate, como premio a su persistencia :p
        // Dividimos los dias que lleva matando mobs y multiplicamos el premio por los dias consecutivos
        if ((_conteo % 100) == 0)
        {
            int bonus = (int) ((System.currentTimeMillis() - (Long.valueOf(st.getGlobalQuestVar("Q1003_Uvez")))) / 86400000);
            if (bonus < 1)
            {
                bonus = 1;
            }
            killer.sendMessage("Juega cada dia para acumular bonos a los premios! Bonos acumulados: " + String.valueOf(bonus));
            killer.addItem("Q1003_MataMata", 57, 1 * bonus, killer, true);
        }
        
        // Aqui el Mob que te cuenta los mobs que llevas
        // se activa al empezar a matar.
        // Si no llevas contador, te pongo uno!
        if (_listaDePjsConContadores.get(killer.getObjectId()) == null)
        {
            L2NpcTemplate mobContadorTemplate = NpcTable.getInstance().getTemplate(_contadorTemplateID);
            // Hay creadas excepciones en la clase L2Spawn, estas obligado a manejarlas, eso se hace con un try-catch
            try
            {
                // Obtenemos el template del mob que queremos spawnear y las coordenadas, las del PJ
                L2Npc mobContador;
                
                L2Spawn spawn = new L2Spawn(mobContadorTemplate);
                spawn.setLocx(killer.getX());
                spawn.setLocy(killer.getY());
                spawn.setLocz(killer.getZ());
                spawn.setAmount(1);
                spawn.setHeading(killer.getHeading());
                // Por si algun PJ mata nuestro contador, le ponemos un tiempo de respawn
                spawn.setRespawnDelay(15);
                // Instaciado en zona global, ID=0
                spawn.setInstanceId(0);
                // Finalmente lo mostramos
                spawn.init();
                
                // Una IA para que siga al PJ, sino, se quedara estatico
                // Lo primero, obtener el NPC creado
                mobContador = spawn.getLastSpawn();
                // Del NPC obtenemos su IA
                L2CharacterAI mobContadorAI = mobContador.getAI();
                // Ponemos en el target del mob al PJ
                mobContador.setTarget(killer);
                // Ahora seleccionar una de las IA creadas en el server
                mobContadorAI.setIntention(CtrlIntention.AI_INTENTION_FOLLOW);
                mobContadorAI.startFollow(killer);
                
                // Se añade el PJ con su mobContador, para que no siga creando mobContadores
                // put<PJ,Mob>
                _listaDePjsConContadores.put(killer.getObjectId(), (L2NpcInstance) mobContador);
                
                // Rescribimos el titulo del mobContador
                mobContador.setTitle(String.valueOf(_conteo));
                // Mandamos la info actualizada al PJ que sigue
                // mobContador.sendInfo(killer);
                sendInRadius(mobContador);
                
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        // Si lleva contador, solo editamos el title del mobContador
        else if (_listaDePjsConContadores.get(killer.getObjectId()) != null)
        {
            // Si por algun motivo no existiese el mobContador, hay que manejar la excepcion para que el server no se colapse.
            try
            {
                _listaDePjsConContadores.get(killer.getObjectId()).setTitle(String.valueOf(_conteo));
                // _listaDePjsConContadores.get(killer.getObjectId()).sendInfo(killer);
                sendInRadius(_listaDePjsConContadores.get(killer.getObjectId()));
            }
            catch (Exception e)
            {
                // Esto mostrara el origen de la excepcion.
                // Es lo que hay que mirar con atencion en caso de que algo falle
                e.printStackTrace();
            }
        }
        return null;
    }
    
    private void sendInRadius(L2Npc mobContador)
    {
        Collection<L2Character> list;
        list = mobContador.getKnownList().getKnownCharacters();
        for (L2Character pj : list)
        {
            if (!(pj instanceof L2PcInstance))
            {
                continue;
            }
            mobContador.sendInfo((L2PcInstance) pj);
        }
    }
    
    private void valoresQuest(QuestState st, L2PcInstance player, int caso)
    {
        switch (caso)
        {
        // Case 0, inicializa los valores de la quest
            case (0):
                st = player.getQuestState(Q1003_MataMata.class.getSimpleName());
                // Inicializo las variables globales de la quest
                st.saveGlobalQuestVar("Q1003_Pvez", String.valueOf(System.currentTimeMillis())); // Primera vez que participo
                st.saveGlobalQuestVar("Q1003_Uvez", "0"); // Ultima vez que participo
                st.saveGlobalQuestVar("Q1003_MobsMuertos", "0"); // Mobs muertos en total
                // Marco la quest como creada, asi se que todavia no ha empezado a matar mobs
                st.setState(State.CREATED);
                break;
            // Case 1, carga los valores guardados de la quest
            case (1):
                _conteo = Integer.valueOf(st.getGlobalQuestVar("Q1003_MobsMuertos"));
                if ((System.currentTimeMillis() - Long.valueOf(st.getGlobalQuestVar("Q1003_Uvez"))) > 86400000)
                {
                    player.sendMessage("Ya ha pasado mas de un dia desde tu ultimo entrenamiento! Has perdido tu bonificacion!");
                    st.saveGlobalQuestVar("Q1003_Pvez", String.valueOf(System.currentTimeMillis()));
                }
                break;
        }
        
    }
    
    public static void main(String[] args)
    {
        new Q1003_MataMata(1003, Q1003_MataMata.class.getSimpleName(), "MataMata");
    }
}*/